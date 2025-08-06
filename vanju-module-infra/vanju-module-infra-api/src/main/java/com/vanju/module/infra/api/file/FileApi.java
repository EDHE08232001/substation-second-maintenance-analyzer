package com.vanju.module.infra.api.file;

/**
 * 文件 API 接口
 *
 * @author 万炬源码
 */
public interface FileApi {

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(byte[] content) {
        return createFile(null, null, content);
    }

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param path 文件路径
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(String path, byte[] content) {
        return createFile(null, path, content);
    }

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param name    文件名称
     * @param path    文件路径
     * @param content 文件内容
     * @return 文件路径
     */
    String createFile(String name, String path, byte[] content);

    /**
     * @param id
     * @return
     */
    void deleteFileBatch(Long[] id) throws Exception;

    /**
     * 上传文件，返回文件id
     *
     * @param name
     * @param path
     * @param content
     * @return
     */
    String createFileReturnId(String name, String path, byte[] content);
}
