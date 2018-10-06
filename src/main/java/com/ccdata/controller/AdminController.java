package com.ccdata.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONArray;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ccdata.domain.Admin;
import com.ccdata.service.AdminService;
import com.ccdata.util.ResponseUtil;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ccdata.util.APIResult;
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @ResponseBody
    @RequestMapping("/testJavaBean")
    public APIResult test(){
        Admin admin = new Admin();
        admin.setId(1);
        admin.setUsername("asd");
        admin.setPassword("789456123");
        return APIResult.createOk(admin);
    }


    @ResponseBody
    @RequestMapping("/json")
    public Map test2(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "1");
        map.put("test",123);
        map.put("array", new String[]{"a","b","c"});
        return map;
    }

    @ResponseBody
    @RequestMapping("/testjson")
    public Admin test3(){
        Admin admin = new Admin();
        admin.setId(1);
        admin.setUsername("asd");
        admin.setPassword("789456123");
        return admin;
    }


    @RequestMapping("/mymy")
    public String my(Model model) throws Exception{
        model.addAttribute("name","测1试");
        Map<String,String> map= new HashMap<String,String>();
        map.put("1o","2o");
        map.put("33","44");
        model.addAttribute("allProducts",map);
        return "myVelocity";
    }

    @RequestMapping("one")
    public ModelAndView mymy(){
        ModelAndView modelAndView = new ModelAndView("myVelocity");
        modelAndView.addObject("name","sfd");
        return modelAndView;
    }

    @RequestMapping("/my")
    public String my() throws Exception{
        return "mypage";
    }

    /**
     *
     * @param admin
     * @param request
     * @param session
     * @return
     */

    @RequestMapping("/login")
    public String login(Admin admin, HttpServletRequest request,
                        HttpSession session){
        Admin resultAdmin = adminService.login(admin);

        if(resultAdmin == null) {
            request.setAttribute("admin",admin);
            request.setAttribute("errorMsg",
                    "Please check your username and password!");
            return "login";
        } else {
            session.setAttribute("currentAdmin",resultAdmin);
            session.setAttribute("username",resultAdmin.getUsername());
            return "redirect:main";
        }
    }


    /**
     *
     * @param model
     * @return
     * @throws Exception
     */


    @RequestMapping(value="/main")
    public String test(Model model) throws Exception{
        return "mypage";
    }


    /**
     *
     * @param admin
     * @param response
     * @return
     * @throws Exception
     */


    @RequestMapping("/list")
    public String list(Admin admin, HttpServletResponse response)
            throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        if (admin.getUsername() != null
                && !"".equals(admin.getUsername().trim())) {
            map.put("username","%" + admin.getUsername() + "%");
        }

        List<Admin> adminList = adminService.findAdmins(map);
        Integer total = adminService.getCount(map);

        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(adminList);
        result.put("rows",jsonArray);
        result.put("total", total);
        ResponseUtil.write(response, result);
        return null;
    }





    @RequestMapping("/save")
    public String save(Admin admin, HttpServletRequest request,
                       HttpServletResponse response) throws Exception {
        int resultTotal = 0;

        if (admin.getId() == null)
            resultTotal = adminService.addAdmin(admin);
        else
            resultTotal = adminService.updateAdmin(admin);

        JSONObject result = new JSONObject();
        if (resultTotal > 0) {
            result.put("success", true);
        }else {
            result.put("success",false);
        }
        ResponseUtil.write(response,result);
        return null;
    }



    @RequestMapping("/delete")
    public String delete(@RequestParam(value = "ids") String ids,
                         HttpServletResponse response ,HttpSession session) throws Exception {
        JSONObject result = new JSONObject();
        String[] idsStr = ids.split(",");
        for (int i = 0; i < idsStr.length; i++) {

            if (idsStr[i].equals("1")||idsStr[i].equals(((Admin)session.getAttribute("currentAdmin")).getId().toString())){
                result.put("success",false);
                continue;
            }else{
                adminService.deleteAdmin(Integer.parseInt(idsStr[i]));
                result.put("success",true);
            }

        }
        ResponseUtil.write(response, result);
        return null;
    }



    @RequestMapping("/logout")
    public String logout(HttpSession session) throws Exception {
        session.invalidate();
        return "redirect:/login.jsp";
    }




}
