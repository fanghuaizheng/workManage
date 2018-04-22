package com.xawl.Service.imp;

import com.xawl.Dao.DclassDao;
import com.xawl.Dao.LessonworkDao;
import com.xawl.Dao.UserDao;
import com.xawl.Pojo.Dclass;
import com.xawl.Pojo.Lessonwork;
import com.xawl.Pojo.User;
import com.xawl.Service.LessonworkService;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

@Service
public class LessonworkServiceImp implements LessonworkService {

    @Resource
    LessonworkDao lessonworkDao;
    @Resource
    DclassDao dclassDao;

    @Override
    public List<Lessonwork> getLessonwork(Lessonwork lessonwork) {
        List<Lessonwork> lessonworksList=lessonworkDao.getLessonwork(lessonwork);
        System.out.println("lessonworksList.size()"+lessonworksList.size());
        //if(lessonworksList.size()==0) return null;
        for(int i=0;i<lessonworksList.size();i++){
            Dclass dclass = new Dclass();
            dclass.setId(lessonworksList.get(i).getCid());
            System.out.println("Dclass.id:" + dclass.getId());
            dclass = dclassDao.getDclass(dclass).get(0);
            lessonworksList.get(i).setCname(dclass.getSeries() + dclass.getCname());//班级姓名
            lessonworksList.get(i).setCnum(dclass.getPnum());//班级人数
            if(lessonworksList.get(i).getPart()!=null&&lessonworksList.get(i).getPart()!=""){
                System.out.println("lessonworkList.get(" + i + ").getId():" + lessonworksList.get(i).getId());
                String[] pnums = lessonworksList.get(i).getPart().split(",");
                System.out.println("pnums.length:"+pnums.length);
                for (int j = 0; j < pnums.length; j++) {
                 if(pnums[j]!=null&&pnums[j]!=""){
                     Dclass dclass1 = new Dclass();
                     System.out.println("pnums[" + j + "]:" + pnums[j]);
                     dclass1.setId(Integer.parseInt(pnums[j]));
                     System.out.println("dclass1.getId:" + dclass1.getId());
                     System.out.println("dclassDao.getDclass(dclass1).get(0):" + dclassDao.getDclass(dclass1).get(0));
                     dclass1 = dclassDao.getDclass(dclass1).get(0);
                     lessonworksList.get(i).setCname( lessonworksList.get(i).getCname()+","+dclass.getSeries()+dclass1.getCname());//班级姓名
                     lessonworksList.get(i).setCnum(lessonworksList.get(i).getCid()+dclass1.getPnum());//班级人数
                 }
                }
            }
        }
        return lessonworksList;
    }

    @Override
    public void insertLessonwork(Lessonwork lessonwork) {
        lessonworkDao.insertLessonwork(lessonwork);

    }

    @Override
    public void updateLessonworkById(Lessonwork lessonwork) {

        lessonworkDao.updateLessonworkById(lessonwork);
    }

    @Override
    public void deleteLessonworkById(Integer id) {
        lessonworkDao.deleteLessonworkById(id);

    }

    @Transactional
    @Override
    public String exportTestwork(HttpServletRequest request, Lessonwork lessonwork) {
        lessonwork.setPass(2);
        List<Lessonwork> lessonworkList =getLessonwork(lessonwork);
        Calendar a = Calendar.getInstance();
        System.out.println(a.get(Calendar.YEAR));
        String fileName = a.get(Calendar.YEAR) + "第" + lessonwork.getTerm() + "学期课程工作量统计.xls";
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建工作表
        HSSFSheet sheet = workbook.createSheet("sheet1");
        HSSFRow rows = sheet.createRow(0);
        rows.createCell(0).setCellValue("姓名");
        rows.createCell(1).setCellValue("职称");
        rows.createCell(2).setCellValue("课程名字（普通）");
        rows.createCell(3).setCellValue("任课班级");
        rows.createCell(4).setCellValue("班级人数");
        rows.createCell(5).setCellValue("拆班/合班");
        rows.createCell(6).setCellValue("计划学时");
        rows.createCell(7).setCellValue("系数");
        rows.createCell(8).setCellValue("标准课时");
        rows.createCell(9).setCellValue("课程名字（实验）");
        rows.createCell(10).setCellValue("任课班级");
        rows.createCell(11).setCellValue("班级人数");
        rows.createCell(12).setCellValue("实验课时");
        rows.createCell(13).setCellValue("标准课时");
        rows.createCell(14).setCellValue("课时合计");
        System.out.println("lessonworkList.size():" + lessonworkList.size());
        int i = 0;//控制lessonList的行
        int uid=0;
        for (int row = 1; row <= lessonworkList.size(); row++) {//控制行
            Double pclassSum = 0.0;//总课时
            uid=lessonworkList.get(i).getUid();
            rows = sheet.createRow(row);
            System.out.println("User.name:" + lessonworkList.get(i).getUser().getName());
            rows.createCell(0).setCellValue(lessonworkList.get(i).getUser().getName());//当前用户姓名
            rows.createCell(1).setCellValue(lessonworkList.get(i).getUser().getLevel());//当前用户职称
            System.out.println("dclassName:" + lessonworkList.get(i).getCname());
            System.out.println("dclassPNum:" + lessonworkList.get(i).getCnum());
            if (lessonworkList.get(i).getType() == 1 || lessonworkList.get(i).getType() == 2) {//如果不是实验课
                rows.createCell(2).setCellValue(lessonworkList.get(i).getLname());//课程名
                rows.createCell(5).setCellValue("拆班");
                System.out.println("lessonworkList.get(" + i + ").getPart():" + lessonworkList.get(i).getPart());
                // System.out.println("lessonworkList.get("+i+").getPart().length():"+ lessonworkList.get(i).getPart().length());
                if (lessonworkList.get(i).getPart() != null && lessonworkList.get(i).getPart() != "") {
                    rows.createCell(5).setCellValue("合班");//班级名
                }
                rows.createCell(3).setCellValue(lessonworkList.get(i).getCname());//班级名
                rows.createCell(4).setCellValue(lessonworkList.get(i).getCnum());//班级人数
                rows.createCell(6).setCellValue(lessonworkList.get(i).getPclasshours());//计划学时
                rows.createCell(7).setCellValue(lessonworkList.get(i).getCoe());//系数
                rows.createCell(8).setCellValue(lessonworkList.get(i).getClasshours());//标准学时
                pclassSum+= lessonworkList.get(i).getClasshours();
                i++;
                if (i >= lessonworkList.size()) {
                    // row = i;
                    break;
                }
                if (uid!=lessonworkList.get(i).getUid()) {
                    // row = i;
                    continue;
                }
            }
            if (lessonworkList.get(i).getType() == 3) {
                rows.createCell(9).setCellValue(lessonworkList.get(i).getLname());//课程名
                rows.createCell(10).setCellValue(lessonworkList.get(i).getCname());//任课班级
                rows.createCell(11).setCellValue(lessonworkList.get(i).getCnum());//班级人数
                rows.createCell(12).setCellValue(lessonworkList.get(i).getPclasshours());//实验课时
                rows.createCell(13).setCellValue(lessonworkList.get(i).getClasshours());//标准课时
                pclassSum += lessonworkList.get(i).getClasshours();
                i++;
                if (i >= lessonworkList.size()) {
                    break;
                }
                if (uid!=lessonworkList.get(i).getUid()) {
                    // row = i;
                    continue;
                }
            }
            rows.createCell(14).setCellValue(pclassSum);
        }
        String path = request.getSession().getServletContext().getRealPath("files");
        System.out.println("path：" + path);
        try {
            File xlsFile = new File(path, fileName);
            FileOutputStream xlsStream = new FileOutputStream(xlsFile);
            workbook.write(xlsStream);
            lessonworkDao.updateLessonworkByPass(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path  + "\\"+ fileName;
    }
}