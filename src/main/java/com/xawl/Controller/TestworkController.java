package com.xawl.Controller;

import com.xawl.Pojo.Coe;
import com.xawl.Pojo.Testwork;
import com.xawl.Service.TestworkService;
import com.xawl.Vo.ResultData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Controller
public class TestworkController {
    @Resource
    TestworkService testworkService;

    @RequestMapping("/user/insertTestworkJnum.action")
    @ResponseBody
    ResultData insertTestworkJnum(HttpSession session, Testwork testwork) {
        if (testwork == null || testwork.getJnum() == null)
            return new ResultData(23);
        Testwork testwork1 = new Testwork();
        testwork1.setUid((Integer) session.getAttribute("uid"));
        //System.out.println("uid:"+testwork1.getUid());
        testwork1.setTerm(testwork.getTerm());

        testwork.setUid(testwork1.getUid());
        testwork.setJpclass(testwork.getJnum() * Coe.invigilate);
        testwork.setPclassNum(testwork.getJpclass());
        testwork.setPass(0);
        testwork.setStatedDate(new Timestamp(new Date().getTime()));
        List<Testwork> testworkList = testworkService.getTestwork(testwork1);//查询此用户之前是否插入过考试工作量的数据,在同一学期
        if(testworkList!=null&&testworkList.size()>0&&testworkList.get(0).getPass()!=4) {
            if (testworkList.get(0).getJnum() != null && testworkList.get(0).getJnum() > 0)
                return new ResultData(24, "existed");
            else {//如果已存在课程名插入就得更新总学时了
                testwork.setId(testworkList.get(0).getId());
                testwork.setPclassNum(testwork.getJpclass() + testworkList.get(0).getPclassNum());
                testworkService.updateTestworkById(testwork);
                return new ResultData(1);
            }
        }
        System.out.println("uid:"+testwork.getUid());
        System.out.println("getJnum:"+testwork.getJnum());
        System.out.println("getJpclass:"+testwork.getJpclass());
        System.out.println("getPclassNum:"+testwork.getPclassNum());
        testworkService.insertTestwork(testwork);
        return new ResultData(1);

    }

    @RequestMapping("/user/insertTestwork.action")
    @ResponseBody
    ResultData insertTestwork(HttpSession session, Testwork testwork) {
        if (testwork == null)
            return new ResultData(23);
        if (testwork.getLname() == null || testwork.getLname() == "")
            return new ResultData(26, "课程名字为空");
        testwork.setPass(0);//pass设置为未审核
        testwork.setUid((Integer) session.getAttribute("uid"));
        System.out.println("uid:" + testwork.getUid());
        testwork.setStatedDate(new Timestamp(new Date().getTime()));
        //计算标准课时
        if (testwork.getMpclass() != null&&testwork.getMpclass()>0) {
            testwork.setMpclass(Coe.testPaper);
        }
        if (testwork.getQpclass() != null&&testwork.getQpclass()>0) {
            testwork.setQpclass(Coe.testPaper);
        }
        System.out.println("testWork.getMpclass:" + testwork.getMpclass());
        System.out.println("testwork.getQpclass():" + testwork.getQpclass());
        testwork.setPaperSum(testwork.getBpaperNum() + testwork.getCpaperNum() + testwork.getQpaperNum() + testwork.getPaperNum());
        System.out.println("阅卷总份数:" + testwork.getPaperSum());
        testwork.setPaperPclass(testwork.getPaperNum() * Coe.inspectTest);
        testwork.setPclassNum(testwork.getPaperSum() + testwork.getQpclass() + testwork.getPaperPclass() + testwork.getJpclass());
        System.out.println("总课时：" + testwork.getPclassNum());
        Testwork testwork1 = new Testwork();
        testwork1.setUid(testwork.getUid());
        testwork1.setTerm(testwork.getTerm());
        List<Testwork> testworksList = testworkService.getTestwork(testwork1);
        if (testworksList != null && testworksList.size() > 0&&testwork.getPass()!=4) {
            System.out.println("0000:" + testworksList);
            if (testwork.getLname() == testworksList.get(0).getLname())
                return new ResultData(24, "existed");
            else {
                testwork.setPclassNum(testwork.getPclassNum() + testworksList.get(1).getJpclass());
                testwork.setId(testworksList.get(1).getId());
                testworkService.updateTestworkById(testwork);
                return new ResultData(1);
            }

        }
        testworkService.insertTestwork(testwork);
        return new ResultData(1);
    }

     /*
     计算标准课时
     * */

  /*  Double calculateClasshours(Integer type, Integer num) {
        Double Classhours = 0.0;
        if (type == 1 || type == 2)
            Classhours = (double) num * Coe.testPaper;
        if (type == 3)
            Classhours = (double) num * Coe.invigilate;
        if (type == 4 || type == 5 || type == 6 || type == 7)
            Classhours = (double) num * Coe.inspectTest;
        return Classhours;
    }*/

    @RequestMapping("/user/updateTestworkById.action")
    @ResponseBody
    ResultData updateTestworkById(Testwork testwork) {
        if (testwork == null && testwork.getId() == null)
            return new ResultData(23);
        if (testwork.getJnum() != null && testwork.getJnum() > 0) {
            testwork.setJpclass(testwork.getJnum() * Coe.invigilate);
        }
        if ((testwork.getQpaperNum() != null && testwork.getQpaperNum() > 0) || (testwork.getCpaperNum() != null && testwork.getCpaperNum() > 0)
                || (testwork.getBpaperNum() != null && testwork.getBpaperNum() > 0) || (testwork.getPaperNum() != null && testwork.getPaperNum() > 0)) {
            testwork.setPaperSum(testwork.getBpaperNum() + testwork.getCpaperNum() + testwork.getQpaperNum() + testwork.getPaperNum());
            testwork.setPaperPclass(testwork.getPaperSum() * Coe.inspectTest);
        }
        if (testwork.getMpclass() != null && testwork.getMpclass() > 0) {
            testwork.setMpclass(Coe.testPaper);
        }
        if (testwork.getQpclass() != null && testwork.getQpclass() > 0) {
            testwork.setQpclass(Coe.testPaper);
        }
        testwork.setPclassNum(testwork.getPaperSum() + testwork.getQpclass() + testwork.getPaperPclass() + testwork.getJpclass());
        System.out.println("总课时：" + testwork.getPclassNum());
        testworkService.updateTestworkById(testwork);
        return new ResultData(1);
    }

    @RequestMapping("/user/getTestwork.action")
    @ResponseBody
    ResultData getTestwork(Testwork testwork,HttpSession session) {
        if (testwork == null)
            testwork.setUid((Integer) session.getAttribute("uid"));

        return new ResultData(1, testworkService.getTestwork(testwork));
    }

    @RequestMapping("/admin/deleteTestwork.action")
    @ResponseBody
    ResultData delectTestwork(Integer id) {
        if (id == null || id <= 0) {
            return new ResultData(23);
        }
        testworkService.deleteTestworkById(id);
        return new ResultData(1);

    }

    @RequestMapping("/admin/exportTestwork.action")
    @ResponseBody
    ResultData exportTestwork(HttpServletRequest request,
                              HttpServletResponse response) {
        testworkService.exportTestwork(request);
        return new ResultData(1);
    }
}

