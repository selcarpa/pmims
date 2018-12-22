package pmim.service;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import pmim.mapper.proposerMapper;
import pmim.mapper.uploadInstructionMapper;
import pmim.model.proposer;
import pmim.model.responseMessage;
import pmim.model.uploadInstruction;
import pmim.model.user;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class proposerService {
    @Autowired
    proposerMapper pm;
    @Autowired
    uploadInstructionMapper upm;

    public Object initProposerUserPage(user currentUser) {
        Map m = new HashMap();
        List ul;
        proposer currentProposer;
        Map result = new HashMap();
        try {
            ul = upm.selectUploadInstructionByPosition(new uploadInstruction(1));
            currentProposer = pm.selectProposerById(currentUser);
            result.put("uploadInstructions", ul);
            result.put("currentProposer", currentProposer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONObject.fromObject(new responseMessage(0, "", result)).toString();
    }

    public Object uploadFile(HttpServletRequest request, String currentUser) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        //检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(request)) {
            //将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            //获取multiRequest 中所有的文件名
            Iterator iter = multiRequest.getFileNames();
            while (iter.hasNext()) {
                //一次遍历所有文件
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file != null) {
                    String path = "D:/idea project/pmims/uploadPath/" + file.getOriginalFilename();
                    //上传
                    try {
                        file.transferTo(new File(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return JSONObject.fromObject(new responseMessage(1,"上传失败",null)).toString();
                    }
                }
            }
        }
        return JSONObject.fromObject(new responseMessage(0,"上传成功",null)).toString();
    }
}
