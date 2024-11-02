package com.zuu.zojbackendquestion.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuu.domain.po.Tag;
import com.zuu.zojbackendquestion.mapper.TagMapper;
import com.zuu.zojbackendquestion.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author zuu
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2024-10-19 16:10:34
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




