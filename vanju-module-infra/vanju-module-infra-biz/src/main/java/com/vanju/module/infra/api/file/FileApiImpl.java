package com.vanju.module.infra.api.file;

import com.vanju.module.infra.service.file.FileService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 文件 API 实现类
 *
 * @author 万炬源码
 */
@Service
@Validated
public class FileApiImpl implements FileApi {

    @Resource
    private FileService fileService;

    @Override
    public String createFile(String name, String path, byte[] content) {
        return fileService.createFile(name, path, content);
    }

    /**
     * 批量删除文件
     *
     * @param ids
     * @throws Exception
     */
    @Override
    public void deleteFileBatch(Long[] ids) throws Exception {
        for (Long id : ids) {
            fileService.deleteFile(id);
        }
    }

    /**
     * 保存文件，返回文件id
     *
     * @param name
     * @param path
     * @param content
     * @return
     */
    @Override
    public String createFileReturnId(String name, String path, byte[] content) {
        return fileService.createFileRetrunId(name, path, content);
    }
}
