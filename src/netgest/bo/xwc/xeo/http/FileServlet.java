package netgest.bo.xwc.xeo.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boSession;
import netgest.io.iFile;

public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = 6242459712024507970L;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FileObject fileObject = decodeRequest(request);
		downloadFile(request, response, fileObject);
	}

	private static FileObject decodeRequest(HttpServletRequest request) {
		FileObject result = null;

		if (request != null) {
			try {
				String uri = request.getRequestURI();
				String[] pathElements = uri.trim().split("/");

				if (pathElements.length == 8) {
					FileObject fileObj = new FileObject();
					fileObj.setXeoobject(pathElements[3]);
					fileObj.setBoui(new Long(pathElements[4]).longValue());
					fileObj.setXeoattribute(pathElements[5]);
					fileObj.setFilename(URLDecoder.decode(pathElements[6], "UTF-8"));
					fileObj.setFileid(pathElements[7]);

					result = fileObj;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private void downloadFile(HttpServletRequest request, HttpServletResponse response, FileObject fileObject) {
		EboContext ctx = null;

		try {
			if (fileObject != null) {
				ctx = boApplication.currentContext().getEboContext();

				if (ctx == null) {
					boSession session = (boSession) request.getSession().getAttribute("boSession");

					if (session == null) {
						session = boApplication.getApplicationFromStaticContext("XEO").boLogin("ROBOT", boLoginBean.getSystemKey());
					}

					ctx = session.createRequestContext(request, response, null);
				}

				if (ctx != null) {
					boObject fileobj = boObject.getBoManager().loadObject(ctx, fileObject.getBoui());

					if (fileobj != null && fileobj.exists() && fileobj.getName().equals(fileObject.getXeoobject())) {
						iFile file = fileobj.getAttribute(fileObject.getXeoattribute()).getValueiFile();

						if (file != null && file.exists() && file.isFile() && file.getName().equals(fileObject.getFilename()) && file.getId().equals(fileObject.getFileid())) {
							response.reset();

							Date lastModified = null;

							if (fileobj.getAttribute("SYS_DTSAVE") != null) {
								lastModified = fileobj.getAttribute("SYS_DTSAVE").getValueDate();
							}

							if (lastModified != null) {
								long modifiedSince = request.getDateHeader("If-Modified-Since");
								long lastModifiedLong = lastModified.getTime();

								if (Math.abs(lastModifiedLong - modifiedSince) < 10000) {
									response.sendError(HttpServletResponse.SC_NOT_MODIFIED, "Not modififed");
									return;
								}

								response.setDateHeader("Last-Modified", lastModified.getTime());
								response.setHeader("Cache-Control", "public");
							}

							String bfilename = file.getName();
							String mimetype = getServletContext().getMimeType(bfilename.toLowerCase());
							Long FileSize = new Long(file.length());
							int xfsize = FileSize.intValue();
							response.setContentType(mimetype);
							response.setHeader("Content-disposition", "attachment; filename=" + bfilename);
							response.setContentLength(xfsize);
							int rb = 0;

							ServletOutputStream so = response.getOutputStream();
							InputStream is = null;

							try {
								is = file.getInputStream();
								byte[] a = new byte[4 * 1024];

								while ((rb = is.read(a)) > 0) {
									so.write(a, 0, rb);
								}
							} catch (Exception e) {
								// ignore
							} finally {
								if (is != null) {
									is.close();
								}

								if (so != null) {
									so.close();
								}
							}
						} else {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST);
						}
					} else {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					}
				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
	}

	private static class FileObject {
		private String xeoobject = null;
		private long boui;
		private String xeoattribute = null;
		private String fileid = null;
		private String filename = null;

		public FileObject() {
		}

		public String getXeoobject() {
			return xeoobject;
		}

		public void setXeoobject(String xeoobject) {
			this.xeoobject = xeoobject;
		}

		public long getBoui() {
			return boui;
		}

		public void setBoui(long boui) {
			this.boui = boui;
		}

		public String getXeoattribute() {
			return xeoattribute;
		}

		public void setXeoattribute(String xeoattribute) {
			this.xeoattribute = xeoattribute;
		}

		public String getFileid() {
			return fileid;
		}

		public void setFileid(String fileid) {
			this.fileid = fileid;
		}

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}
	}
}