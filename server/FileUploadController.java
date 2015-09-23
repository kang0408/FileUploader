import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.g6.laas.server.database.repository.IFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@RestController
public class FileUploadController {

    @Autowired
    private ServletContext context;

    @Autowired
    private IFileRepository fileRepository;

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public
    @ResponseBody
    String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public
    @ResponseBody
    Collection<UploadResult> handleFileUpload(
            @RequestParam("files[]") MultipartFile[] files, HttpServletRequest request) {
        String uploadedPath = context.getRealPath("/upload");
        Collection<UploadResult> results = new ArrayList<>();
        for (MultipartFile file : files) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String todayFolder = dateFormatter.format(new Date());
            String fileName = file.getOriginalFilename();
            try {
                String generatedName = UUID.randomUUID().toString();
                String path = uploadedPath + "/" + todayFolder + "/" + generatedName;
                File uploaded = new File(path);
                Files.createParentDirs(uploaded);
                long size = file.getSize();
                InputStream inputStream = file.getInputStream();
                OutputStream outputStream = new FileOutputStream(path);
                ByteStreams.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                results.add(new UploadResult(-1L, fileName, -1L, "failed"));
            }

        }
        return results;
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class UploadResult {
    Long id;
    String fileName;
    Long size;
    String status;
}
