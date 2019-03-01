package pmim.controller;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pmim.model.RequestAction;
import pmim.model.ResponseMessage;
import pmim.model.Student;
import pmim.model.SysUser;
import pmim.service.PermissionCheckService;
import pmim.service.StudentService;
import pmim.service.UserPathService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

@RequestMapping(value = "/student")
@Controller
public class studentCtrl {
    @Autowired
    StudentService ss;
    @Autowired
    UserPathService ups;
    @Autowired
    PermissionCheckService pcs;

    @RequestMapping(value = "/studentInfo.do", produces = "text/html;charset=UTF-8")
    public @ResponseBody
    Object studentInfo(HttpServletRequest request, @RequestBody String jsonstr) {
        RequestAction ra = (RequestAction) JSONObject.toBean(JSONObject.fromObject(jsonstr), RequestAction.class);
        if ("initPage".equals(ra.getAction())) {
            return JSONObject.fromObject(new ResponseMessage(0, "", ss.initPage(request.getSession().getAttribute("currentSysUser")))).toString();
        } else if ("getAllStudentInfo".equals(ra.getAction())) {
            if (pcs.permissionCheck(5, request) || pcs.permissionCheck(6, request)) {
                JsonConfig jsonConfig = new JsonConfig();
                jsonConfig.registerJsonValueProcessor(Date.class, new JsonValueProcessor() {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年mm月dd");

                    @Override
                    public Object processArrayValue(Object o, JsonConfig jsonConfig) {
                        return simpleDateFormat.format(o);
                    }

                    @Override
                    public Object processObjectValue(String s, Object o, JsonConfig jsonConfig) {
                        if (o != null) {
                            return simpleDateFormat.format(o);
                        } else {
                            return null;
                        }
                    }
                });

                return JSONObject.fromObject(new ResponseMessage(0, "", ss.getAllStudentInfo()), jsonConfig).toString();
            } else {
                return JSONObject.fromObject(new ResponseMessage(1, "权限存在问题", null)).toString();
            }
        } else if ("getAllAdminInfo".equals(ra.getAction())) {
            if (pcs.permissionCheck(6, request)) {
                return JSONObject.fromObject(new ResponseMessage(0, "", ss.getAllAdminInfo())).toString();
            } else {
                return JSONObject.fromObject(new ResponseMessage(1, "权限存在问题", null)).toString();
            }
        }
        return null;
    }

    @RequestMapping(value = "/saveOrUpdateStudentInfo.do", produces = "text/html;charset=UTF-8")
    public @ResponseBody
    Object saveOrUpdateStudentInfo(HttpServletRequest request, @RequestBody String jsonstr) {
        RequestAction ra = (RequestAction) JSONObject.toBean(JSONObject.fromObject(jsonstr), RequestAction.class);
        if ("sou".equals(ra.getAction())) {
            Student student = (Student) JSONObject.toBean(JSONObject.fromObject(jsonstr), Student.class);
            student.setUserId(((SysUser) request.getSession().getAttribute("currentSysUser")).getUserId());
            return JSONObject.fromObject(new ResponseMessage(0, ss.saveOrUpdateStudentInfo(student), null)).toString();
        }
        return null;
    }

    @RequestMapping(value = "/headImgUpload.do", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object headImgUpload(HttpServletRequest request) {
        SysUser currentSysUser = (SysUser) request.getSession().getAttribute("currentSysUser");
        String userPath = ups.checkUserPath(currentSysUser.getUserId());
        return ss.headImgUpload(request, currentSysUser.getUserId(), userPath);
    }

    @RequestMapping(value = "/headImg.do")
    @ResponseBody
    public String imageShow(HttpServletRequest request, HttpServletResponse response) {
        String path = ups.checkUserPath(((SysUser) request.getSession().getAttribute("currentSysUser")).getUserId()) + "headImg";
        if (!(new File(path).exists())) {
            try {
                path = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("").getPath(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            path = new File(path).getParentFile().getParentFile().getPath() + "\\img\\akari.png";
        }
        File f = new File(path);
        if (f.exists()) {
            int i = 1;
        }
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            fis = new FileInputStream(path);
            os = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024 * 8];
            while ((count = fis.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            fis.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }
}