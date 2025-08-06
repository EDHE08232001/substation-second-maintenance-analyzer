package com.vanju.module.system.api.dept;

import com.vanju.framework.common.util.object.BeanUtils;
import com.vanju.module.system.api.dept.dto.PostRespDTO;
import com.vanju.module.system.dal.dataobject.dept.PostDO;
import com.vanju.module.system.service.dept.PostService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 岗位 API 实现类
 *
 * @author 万炬源码
 */
@Service
public class PostApiImpl implements PostApi {

    @Resource
    private PostService postService;

    @Override
    public void validPostList(Collection<Long> ids) {
        postService.validatePostList(ids);
    }

    @Override
    public List<PostRespDTO> getPostList(Collection<Long> ids) {
        List<PostDO> list = postService.getPostList(ids);
        return BeanUtils.toBean(list, PostRespDTO.class);
    }

}
