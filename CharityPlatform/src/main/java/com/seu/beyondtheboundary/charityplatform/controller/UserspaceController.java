package com.seu.beyondtheboundary.charityplatform.controller;

import com.seu.beyondtheboundary.charityplatform.domain.OrderItem;
import com.seu.beyondtheboundary.charityplatform.domain.Project;
import com.seu.beyondtheboundary.charityplatform.domain.User;
import com.seu.beyondtheboundary.charityplatform.repository.OrderItemRepository;
import com.seu.beyondtheboundary.charityplatform.repository.UserRepository;
import com.seu.beyondtheboundary.charityplatform.service.ProjectService;
import com.seu.beyondtheboundary.charityplatform.service.UserServiceImpl;
import com.seu.beyondtheboundary.charityplatform.util.ConstraintViolationExceptionHandler;
import com.seu.beyondtheboundary.charityplatform.util.MD5;
import com.seu.beyondtheboundary.charityplatform.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户主页空间控制器.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2017年3月25日
 */
@Controller
@RequestMapping("/u")
public class UserspaceController {

    public static final String saveRoot = ClassUtils.getDefaultClassLoader().getResource("static/userimages").getPath(); //"src/main/webapp/userimages/";

    public static final String saveAvatarRoot = ClassUtils.getDefaultClassLoader().getResource("static/userimages/avatar").getPath();
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    //获取单个项目详情的界面
    @GetMapping("/person/project_details")
    public ModelAndView Test(@RequestParam(value = "id") Long id,
                             Model model) {
        Project project = projectService.getProjectById(id);
        System.out.println(project.getContent());
        model.addAttribute("project", project);
        return new ModelAndView("person/project_details", "projectModel", model);
    }

    //@RequestBody Project project
    //String title ,String summary, String content

    /*	@GetMapping("/projects/edit")
        public String show() {
            return "/person/projectedit";
        }*/
    //获取项目的编辑发布项目
    @PostMapping("/projects/edit")
    public ResponseEntity<Response> saveProject(String title, String summary, String content, String htmlContent) {
        System.out.println("before saving project");
        Project project = new Project(title, summary, content, htmlContent);
        Long id = 0L;
        try {
            projectService.saveProject(project);
            id = project.getId();
            System.out.println("This projId is" + id);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        System.out.println("I am saving project");
        String redirectUrl = "personal_center";
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }

    @PostMapping("/projects/edit_complete")
    public ResponseEntity<Response> completeProject(String title, String summary, String content, String id
            , String category, String aimDonation, String createdTime, String endTime, String htmlContent
            , String initiator) {
        System.out.println("before saving project");
        try {
            if (id != null) {
                System.out.println(createdTime);
                Long Lid = Long.parseLong(id);
                Project project = projectService.getProjectById(Lid);
                project.setTitle(title);
                project.setSummary(summary);
                project.setContent(content);
                project.setHtmlContent(htmlContent);
                project.setCategory(Long.parseLong(category));
                project.setAimDonation(Long.parseLong(aimDonation));
                project.setInitiator(initiator);
                project.setCreatedTime(createdTime);
                project.setEndTime(endTime);
                projectService.updateProject(project);
            }
            System.out.println("这里是更新数据中的Project！！！！！！！！！");
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        System.out.println("I am saving project");
        String redirectUrl = "/admins/to_publish";
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }




	/*@GetMapping("/complete_user_info")
    public String complete_user_info() {
        return "/person/complete_personal_information";
    }*/

    @GetMapping("/I_want_verify")
    public String I_want_verify() {
        return "person/I_want_verify";
//		System.out.println(title+"||||||"+summary+"||||||"+content);
//		System.out.println(project.getSummary()+"summary!");
//		System.out.println("I am saving project");
//		String redirectUrl="/u/person/hello";
//		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));

    }

    @GetMapping("/complete_user_info")
    public String complete_user_info() {
        return "person/complete_personal_information";
    }


    @PostMapping("/complete_user_info")
    public String complete_user_info1(User user, HttpServletRequest request, HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        //使用request对象的getSession()获取session，如果session不存在则创建一个
        HttpSession session = request.getSession();
        //将数据存储到session中
        User user1 = (User) session.getAttribute("user");
        user1.setSex(user.isSex());
        if (user.getTel() != "")
            user1.setTel(user.getTel());
        if (user.getAddress() != "")
            user1.setAddress(user.getAddress());
        if (user.getUser_id_card() != "")
            user1.setUser_id_card(user.getUser_id_card());
        if (user.getRealname() != "")
            user1.setRealname(user.getRealname());
        if (user.getEmail() != "")
            user1.setEmail(user.getEmail());
        if (user.getPassword() != "")
            user1.setPassword(MD5.EncoderByMd5(user.getPassword()));
        userRepository.save(user1);
        return "redirect:/personal_center";
    }

    @GetMapping("/user_commit_verify")
    public String user_commit_verify() {
        return "person/I_want_verify";
    }

    @PostMapping("/user_commit_verify")
    public String user_commit_verify1(User user, HttpServletRequest request, HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        //使用request对象的getSession()获取session，如果session不存在则创建一个
        HttpSession session = request.getSession();
        //将数据存储到session中
        User user1 = (User) session.getAttribute("user");
        user1.setSex(user.isSex());
        if (user.getTel() != "")
            user1.setTel(user.getTel());
        if (user.getAddress() != "")
            user1.setAddress(user.getAddress());
        if (user.getUser_id_card() != "")
            user1.setUser_id_card(user.getUser_id_card());
        if (user.getRealname() != "")
            user1.setRealname(user.getRealname());
        if (user.getEmail() != "")
            user1.setEmail(user.getEmail());

        user1.setVerified(2);        //设为2，让该用户进入待审核列表

        userRepository.save(user1);
        return "redirect:/personal_center";
    }

    @PostMapping("/user_commit_image")
    public String user_upload_image(@RequestParam("image") MultipartFile image, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        if (!image.isEmpty()) {
            try {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=UTF-8");
                //使用request对象的getSession()获取session，如果session不存在则创建一个
                HttpSession session = request.getSession();
                //将数据存储到session中
                User user1 = (User) session.getAttribute("user");

                Date day = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

                Files.copy(image.getInputStream(), Paths.get(saveRoot.substring(1, saveRoot.length()), user1.getId().toString() + df.format(day) + image.getOriginalFilename()));
                if(user1.getConfirmation_link() == "" || user1.getConfirmation_link() == null)
                    user1.setConfirmation_link(user1.getId().toString() + df.format(day) + image.getOriginalFilename() + ";");
                else
                    user1.setConfirmation_link(user1.getConfirmation_link() + user1.getId().toString() + df.format(day) + image.getOriginalFilename() + ";");

                userRepository.save(user1);

                redirectAttributes.addFlashAttribute("message", "you successfully uploaded " + image.getOriginalFilename() + "!");
            } catch (IOException | RuntimeException e) {
                redirectAttributes.addFlashAttribute("message", "Failed to upload " + image.getOriginalFilename() + " =>" + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to upload " + image.getOriginalFilename() + " because it was empty");
        }

        return "redirect:/u/user_commit_verify";
    }

    @PostMapping("/user_commit_avatar")
    public String user_upload_avatar(@RequestParam("image") MultipartFile image, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        if (!image.isEmpty()) {
            try {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=UTF-8");
                //使用request对象的getSession()获取session，如果session不存在则创建一个
                HttpSession session = request.getSession();
                //将数据存储到session中
                User user1 = (User) session.getAttribute("user");

                Date day = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

                Files.copy(image.getInputStream(), Paths.get(saveAvatarRoot.substring(1, saveAvatarRoot.length()), user1.getId().toString() + df.format(day) + image.getOriginalFilename()));

                user1.setAvatar(user1.getId().toString() + df.format(day) + image.getOriginalFilename());

                userRepository.save(user1);

                redirectAttributes.addFlashAttribute("message", "you successfully uploaded " + image.getOriginalFilename() + "!");
            } catch (IOException | RuntimeException e) {
                redirectAttributes.addFlashAttribute("message", "Failed to upload " + image.getOriginalFilename() + " =>" + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to upload " + image.getOriginalFilename() + " because it was empty");
        }

        return "redirect:/u/complete_user_info";
    }

    @GetMapping("/user_donate")
    public String user_donate() {
        return "person/user_donate";
    }

    @GetMapping("/user_refund")
    public ModelAndView user_refund(Model model, HttpServletRequest request, HttpServletResponse response) {
        List<OrderItem> orderItemList = orderItemRepository.findAllByRefundStatusAndStatus(0L, 1L);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        //使用request对象的getSession()获取session，如果session不存在则创建一个
        HttpSession session = request.getSession();
        //将数据存储到session中
        User user1 = (User) session.getAttribute("user");

        List<OrderItem> selectOrder = new ArrayList<>();

        for (OrderItem orderItem:
             orderItemList) {
            if(orderItem.getUser().getId() == user1.getId())
                selectOrder.add(orderItem);
        }
        model.addAttribute("userOrderList", selectOrder);

        return new ModelAndView("person/I_want_refund", "userOrderModel", model);
    }

    @GetMapping("/commit_refund/{id}")
    public String commit_refund(@PathVariable("id") Long id) {
        OrderItem orderItem = orderItemRepository.findById(id);
        orderItem.setRefundStatus(2L);
        orderItemRepository.save(orderItem);
        return "redirect:/u/user_refund";
    }
}





