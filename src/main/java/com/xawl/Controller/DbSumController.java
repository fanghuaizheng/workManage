package com.xawl.Controller;

import com.xawl.Pojo.DbSum;
import com.xawl.Service.DbSumService;
import com.xawl.Service.DclassService;
import com.xawl.Vo.ResultData;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class DbSumController {
    //List<Country> list = countryMapper.selectByPageNumSize(user);
    @Resource
    DbSumService dbSumService;

    @RequestMapping("/admin/exportDbSum.action")
    @ResponseBody
    @Transactional
    ResultData getDclass(){
        return  new ResultData(1);
    }
}
