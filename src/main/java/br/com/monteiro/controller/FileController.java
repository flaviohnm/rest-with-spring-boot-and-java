package br.com.monteiro.controller;

import br.com.monteiro.data.vo.v1.UploadFileResponseVO;
import br.com.monteiro.service.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.logging.Logger;

@Tag(name = "File endpoint")
@RestController
@RequestMapping("/api/file/v1")
public class FileController {

    private Logger logger = Logger.getLogger(FileController.class.getName());

    @Autowired
    private FileStorageService service;

    @PostMapping("uploadFile")
    public UploadFileResponseVO uploadFile(@RequestParam("file") MultipartFile file){
        logger.info("Storing file to disk");

        var fileName = service.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/file/v1/downloadFile/")
                .path(fileName)
                .toUriString();

        return
                new UploadFileResponseVO(
                        fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

}
