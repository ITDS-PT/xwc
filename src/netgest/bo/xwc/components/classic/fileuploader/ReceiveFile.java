package netgest.bo.xwc.components.classic.fileuploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.system.Logger;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

public class ReceiveFile  {

	private File uploadDirectory;
    private File temporaryDirectory;

    private static String CONTENT_TYPE = "text/plain";
    private static int RESPONSE_CODE = 200;

    final Logger log = Logger.getLogger(ReceiveFile.class);


    public ReceiveFile(File uploadDirectory, File temporaryDir){
    	this.uploadDirectory = uploadDirectory;
    	this.temporaryDirectory = temporaryDir;
    }
    

    public File process(HttpServletRequest req, 
    		HttpServletResponse resp, ServletContext context) throws IOException
    {
        RequestParser requestParser;
        File outputFile = null;
        try
        {
        	
            resp.setContentType(CONTENT_TYPE);
            resp.setStatus(RESPONSE_CODE);

            if (ServletFileUpload.isMultipartContent(req))
            {
                MultipartUploadParser multipartUploadParser = new MultipartUploadParser(req, temporaryDirectory, context);
                requestParser = RequestParser.getInstance(req, multipartUploadParser);
                outputFile = writeFileForMultipartRequest(requestParser);
                //writeResponse(resp.getWriter(), null, false);
                
            }
            else
            {
                throw new Exception("Only multipart encoded requests are supported!");
            }
        } catch (Exception e)
        {
            log.warn("Problem handling upload request", e);
            if (e instanceof MergePartsException)
            {
                writeResponse(resp.getWriter(), e.getMessage(), true);
            }
            else
            {
                writeResponse(resp.getWriter(), e.getMessage(), false);
            }

        }
        return outputFile;
    }

    private File writeFileForMultipartRequest(RequestParser requestParser) throws Exception
    {
    	File outputFile = new File(uploadDirectory, requestParser.getOriginalFilename()); 
        if (requestParser.getPartIndex() >= 0)
        {
            writeFile(requestParser.getUploadItem().getInputStream(), new File(uploadDirectory, requestParser.getUuid() + "_" + String.format("%05d", requestParser.getPartIndex())), null);

            if (requestParser.getTotalParts()-1 == requestParser.getPartIndex())
            {
                File[] parts = getPartitionFiles(uploadDirectory, requestParser.getUuid());
                
                for (File part : parts)
                {
                    mergeFiles(outputFile, part);
                }
                assertCombinedFileIsVaid(requestParser.getTotalFileSize(), outputFile, requestParser.getUuid());
                deletePartitionFiles(uploadDirectory, requestParser.getUuid());
            }
        }
        else
        {
            writeFile(requestParser.getUploadItem().getInputStream(), outputFile, null);
        }
        return outputFile;
    }

    private void assertCombinedFileIsVaid(int totalFileSize, File outputFile, String uuid) throws MergePartsException
    {
        if (totalFileSize != outputFile.length())
        {
            deletePartitionFiles(uploadDirectory, uuid);
            outputFile.delete();
            throw new MergePartsException("Incorrect combined file size!");
        }

    }


    private static class PartitionFilesFilter implements FilenameFilter
    {
        private String filename;
        PartitionFilesFilter(String filename)
        {
            this.filename = filename;
        }

        @Override
        public boolean accept(File file, String s)
        {
            return s.matches(Pattern.quote(filename) + "_\\d+");
        }
    }

    private static File[] getPartitionFiles(File directory, String filename)
    {
        File[] files = directory.listFiles(new PartitionFilesFilter(filename));
        Arrays.sort(files);
        return files;
    }

    private static void deletePartitionFiles(File directory, String filename)
    {
        File[] partFiles = getPartitionFiles(directory, filename);
        for (File partFile : partFiles)
        {
            partFile.delete();
        }
    }

    private File mergeFiles(File outputFile, File partFile) throws Exception
   	{
   		FileOutputStream fos;
   		FileInputStream fis;
   		byte[] fileBytes;
   		int bytesRead = 0;
   		fos = new FileOutputStream(outputFile, true);
   		fis = new FileInputStream(partFile);
   		fileBytes = new byte[(int) partFile.length()];
   		bytesRead = fis.read(fileBytes, 0,(int)  partFile.length());
   		assert(bytesRead == fileBytes.length);
   		assert(bytesRead == (int) partFile.length());
   		fos.write(fileBytes);
   		fos.flush();
   		fis.close();
   		fos.close();

   		return outputFile;
   	}

    private File writeFile(InputStream in, File out, Long expectedFileSize) throws IOException
    {
        FileOutputStream fos = null;

        try
        {
            fos = new FileOutputStream(out);

            IOUtils.copy(in, fos);

            if (expectedFileSize != null)
            {
                Long bytesWrittenToDisk = out.length();
                if (!expectedFileSize.equals(bytesWrittenToDisk))
                {
                    log.warn("Expected file {} to be {} bytes; file on disk is {} bytes", new Object[] { out.getAbsolutePath(), expectedFileSize, 1 });
                    out.delete();
                    throw new IOException(String.format("Unexpected file size mismatch. Actual bytes %s. Expected bytes %s.", bytesWrittenToDisk, expectedFileSize));
                }
            }

            return out;
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
        finally
        {
            IOUtils.closeQuietly(fos);
        }
    }
    
    

    public void writeResponse(PrintWriter writer, String failureReason, boolean restartChunking)
    {
        if (failureReason == null)
        {
            writer.print("{\"success\": true}");
        }
        else
        {
            if (restartChunking)
            {
                writer.print("{\"error\": \"" + failureReason + "\", \"reset\": true}");
            }
            else
            {
                writer.print("{\"error\": \"" + failureReason + "\"}");
            }
        }
    }

    private class MergePartsException extends Exception
    {
        

		MergePartsException(String message)
        {
            super(message);
        }
    }
	
}
