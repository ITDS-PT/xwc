package netgest.bo.xwc.framework.jsf;

    /**
     * Thanks to the Facelets folks for some of the concepts incorporated
     * into this class.
     */
    import com.sun.faces.RIConstants;

import com.sun.faces.io.FastStringWriter;

import com.sun.faces.util.Util;

import java.io.IOException;
import java.io.Writer;

import javax.faces.application.StateManager;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Thanks to the Facelets folks for some of the concepts incorporated
 * into this class.
 */
public class XUIWriteBehindStateWriter extends Writer {
        // length of the state marker
        private static final int STATE_MARKER_LEN =
              RIConstants.SAVESTATE_FIELD_MARKER.length();

        private static final ThreadLocal<XUIWriteBehindStateWriter> CUR_WRITER =
             new ThreadLocal<XUIWriteBehindStateWriter>();
        
        private Writer out;
        private Writer orig;
        
        private FastStringWriter fWriter;
        private boolean stateWritten;
        private int bufSize;
        private char[] buf;
        private FacesContext context;


        // -------------------------------------------------------- Constructors
        public XUIWriteBehindStateWriter(Writer out, FacesContext context, int bufSize) {
            //this.out = out;
            this.orig = out;
            this.context = context;
            this.bufSize = bufSize;
            this.buf = new char[bufSize];

            this.out = this.fWriter = new FastStringWriter( bufSize );
            
            CUR_WRITER.set(this);
        }

        // ------------------------------------------------- Methods from Writer
        public void write(int c) throws IOException {
            out.write(c);
        }

        public void write(char cbuf[]) throws IOException {
            out.write(cbuf);
        }

        public void write(String str) throws IOException {
            out.write(str);
        }

        public void write(String str, int off, int len) throws IOException {
            out.write(str, off, len);
        }

        public void write(char cbuf[], int off, int len) throws IOException {
            out.write(cbuf, off, len);
        }

        public void flush() throws IOException {
            // no-op
        }

        public void close() throws IOException {
           // no-op
        }

        // ------------------------------------------------------ Public Methods

        public static XUIWriteBehindStateWriter getCurrentInstance() {
            return CUR_WRITER.get();
        }

        public void release() {
            CUR_WRITER.set(null);
        }

        public void writingState() {
            if (!stateWritten) {
                this.stateWritten = true;
                //out = fWriter = new FastStringWriter(1024);
            }
        }

        public boolean stateWritten() {
            return stateWritten;
        }

        /**
         * <p> Write directly from our FastStringWriter to the provided
         * writer.</p>
         * @throws IOException if an error occurs
         */

         public void flushToWriter( ) throws IOException {
            flushToWriter( true );
         }

        public void flushToWriter( boolean saveState ) throws IOException {
            // Save the state to a new instance of StringWriter to
            // avoid multiple serialization steps if the view contains
            // multiple forms.
            StateManager stateManager = Util.getStateManager(context);
            ResponseWriter origWriter = context.getResponseWriter();
            FastStringWriter state =
                  new FastStringWriter((stateManager.isSavingStateInClient(
                        context)) ? bufSize : 128);
            if( saveState )
            {
                context.setResponseWriter(origWriter.cloneWithWriter(state));
                
                if( !context.getViewRoot().isTransient() )
                	stateManager.writeState(context, stateManager.saveView(context));
                
                context.setResponseWriter(origWriter);
            }
            StringBuilder builder = fWriter.getBuffer();
            // begin writing...
            int totalLen = builder.length();
            StringBuilder stateBuilder = state.getBuffer();
            int stateLen = stateBuilder.length();
            int pos = 0;
            int tildeIdx = getNextDelimiterIndex(builder, pos);
            while (pos < totalLen) {
                if (tildeIdx != -1) {
                    if (tildeIdx > pos && (tildeIdx - pos) > bufSize) {
                        // there's enough content before the first ~
                        // to fill the entire buffer
                        builder.getChars(pos, (pos + bufSize), buf, 0);
                        orig.write(buf);
                        pos += bufSize;
                    } else {
                        // write all content up to the first '~'
                        builder.getChars(pos, tildeIdx, buf, 0);
                        int len = (tildeIdx - pos);
                        orig.write(buf, 0, len);
                        // now check to see if the state saving string is
                        // at the begining of pos, if so, write our
                        // state out.
                        if (builder.indexOf(
                              RIConstants.SAVESTATE_FIELD_MARKER,
                              pos) == tildeIdx) {
                            // buf is effectively zero'd out at this point
                            int statePos = 0;
                            while (statePos < stateLen) {
                                if ((stateLen - statePos) > bufSize) {
                                    // enough state to fill the buffer
                                    stateBuilder.getChars(statePos,
                                                          (statePos + bufSize),
                                                          buf,
                                                          0);
                                    orig.write(buf);
                                    statePos += bufSize;
                                } else {
                                    int slen = (stateLen - statePos);
                                    stateBuilder.getChars(statePos,
                                                          stateLen,
                                                          buf,
                                                          0);
                                    orig.write(buf, 0, slen);
                                    statePos += slen;
                                }

                            }
                             // push us past the last '~' at the end of the marker
                            pos += (len + STATE_MARKER_LEN);
                            tildeIdx = getNextDelimiterIndex(builder, pos);
                        } else {
                            pos = tildeIdx;
                            tildeIdx = getNextDelimiterIndex(builder,
                                                             tildeIdx + 1);

                        }
                    }
                } else {
                    // we've written all of the state field markers.
                    // finish writing content
                    if (totalLen - pos > bufSize) {
                        // there's enough content to fill the buffer
                        builder.getChars(pos, (pos + bufSize), buf, 0);
                        orig.write(buf);
                        pos += bufSize;
                    } else {
                        // we're near the end of the response
                        builder.getChars(pos, totalLen, buf, 0);
                        int len = (totalLen - pos);
                        orig.write(buf, 0, len);
                        pos += (len + 1);
                    }
                }
            }

            // all state has been written.  Have 'out' point to the
            // response so that all subsequent writes will make it to the
            // browser.
            out = orig;
        }

        private static int getNextDelimiterIndex(StringBuilder builder,
                                                 int offset) {
            return builder.indexOf(RIConstants.SAVESTATE_FIELD_DELIMITER,
                                   offset);
        }

    }


