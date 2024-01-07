package com.liyifu.clothesdp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyifu.clothesdp.model.entity.Blog;
import com.liyifu.clothesdp.mapper.BlogMapper;
import com.liyifu.clothesdp.service.BlogService;
import org.springframework.stereotype.Service;

/**
* @author liyifu
* @description 针对表【blog】的数据库操作Service实现
* @createDate 2024-01-07 10:47:02
*/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
    implements BlogService {

}




