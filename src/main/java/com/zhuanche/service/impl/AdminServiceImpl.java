package com.zhuanche.service.impl;

import com.zhuanche.dao.AdminDao;
import com.zhuanche.entity.Admin;
import com.zhuanche.service.AdminService;
import com.zhuanche.util.Page;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminDao adminDao;

    @Value("${email.host}")
    private String emailHost;

    @Value("${user.account.create.notice}")
    private String createUserEmailTemplate;

    @Value("${user.resetpwd.content.notice}")
    private String resetPasswordEmailTemplate;

    @Value("${email.port}")
    private String emailport;

    @Value("${email.host.user}")
    private String emailUserName;

    @Value("${email.host.password}")
    private String emailPassword;

    @Value("${crm.host}")
    private String crmHost;

    @Override
    public Admin findByLoginName(String loginName) {
        return adminDao.findByLoginName(loginName);
    }


    @Override
    public Admin findAdminByLoginName(String loginName) {
        return adminDao.findAdminByLoginName(loginName);
    }

    @Override
    public Admin findById(Long id) {
        return adminDao.findById(id);
    }

    @Override
    public Admin findAdminById(Long id) {
        return adminDao.findAdminById(id);
    }

    @Override
    public Long save(Admin admin) {
        return adminDao.save(admin);
    }

    @Override
    public void update(Admin admin) {
        adminDao.update(admin);
    }

    @Override
    public Page<Admin> search(Map<String,Object> params,
                              Integer pageNo, Integer pageSize) {
        if (pageNo<=1){
            params.put("pageNo",0);
        }else{
            params.put("pageNo",(pageNo-1)*pageSize);
        }
        params.put("pageSize",pageSize);
        List<Admin> content = adminDao.search(params);

        Integer total = adminDao.count(params);

        return new Page<Admin>(pageNo,pageSize,total,content);
    }

    @Override
    public List<Admin> findAll() {
        return adminDao.findAll();
    }

    @Override
    public List<Admin> findByName(String name) {
        return adminDao.findByName(name);
    }


    @Override
    public void sendModifyPwdEmail(String email,String name,String loginName,String password) {
        try {
            String content = MessageFormat.format(new String(createUserEmailTemplate.getBytes(),"UTF-8"),name,loginName,password,crmHost);
            sendEmail("帐户创建通知",email,name, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendResetPwdEmail(String email, String name, String loginName, String password) {
        try {
            String content =  MessageFormat.format(new String(resetPasswordEmailTemplate.getBytes(),"UTF-8"),name,loginName,password,crmHost);
            sendEmail("密码重置通知",email,name, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询员工，关联员工角色
     * @param id
     * @return
     */
    @Override
    public Admin selectRoleByAdminPrimaryKey(Long id) {
        return adminDao.selectRoleByAdminPrimaryKey(id);
    }
    private void sendEmail(String title,String email,String name,String content){
        try {
            HtmlEmail simpleEmail = new HtmlEmail();
            //smtp host
            simpleEmail.setHostName(emailHost);
            //登陆邮件服务器的用户名和密码
            simpleEmail.setAuthentication(emailUserName,emailPassword);
            //接收人
            simpleEmail.addTo(email,name);
            //发送人
            simpleEmail.setFrom(emailUserName, "CRM系统");
            //标题
            simpleEmail.setCharset("UTF-8");
            simpleEmail.setSubject(title);
            //邮件内容
            simpleEmail.setMsg(content);
            //发送
            simpleEmail.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
