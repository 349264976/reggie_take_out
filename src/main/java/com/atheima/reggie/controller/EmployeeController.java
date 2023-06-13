package com.atheima.reggie.controller;
import com.atheima.reggie.entity.Employee;
import com.atheima.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.atheima.reggie.common.R;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    /**
     * @param request
     * @param employee
     * @return 2.根据页面提交的用户名username查询数据库
     * 3.如果没有查询到咋返回登陆失败结果
     * 4.密码对面如果密码不一致返回登陆失败
     * 5.查看员工状态若果是禁用状态咋不能反悔员工结果
     * 6.登录成功将yuangongid存入session并且返回登录成功结果
     */
    //员工登录方法
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // * 1.根据页面提交的的passwordMd5加
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //   * 2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<Employee>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(employeeLambdaQueryWrapper);
//             * 3.如果没有查询到咋返回登陆失败结果
        if (emp==null){
            return R.error("登陆失败");
        }
//             * 4.密码对面如果密码不一致返回登陆失败
        if (!emp.getPassword().equals(password)){
            return R.error("密码不一致");
        }
//          * 5.查看员工状态若果是禁用状态咋不能反悔员工结果
        if (emp.getStatus()==0){
            return R.error("用户账户已禁用");
        }
//   * 6.登录成功将yuangongid存入session并且返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    /**
     * 1.清除session的id
     *
     * 2.跳转到登录页面（前端实现）
     *
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工员工信息：{}",employee.toString());
        //设置初始密码需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser((Long)request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name) {
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构建分页构造器
        Page pageinfo=new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageinfo,lambdaQueryWrapper);
        return R.success(pageinfo);
    }

    @PutMapping
    public R<String> update( HttpServletRequest request,@RequestBody Employee employee) {
//        Employee employeebyId=null;
//        if (employee.getId()!=null){
//            if (employee.getStatus()==0){
//                employeebyId = employeeService.getById(employee.getId());
//                employeebyId.setStatus(1);
//                employeeService.updateById(employeebyId);
//            }
//            if (employee.getStatus()==1){
//                employeebyId = employeeService.getById(employee.getId());
//                employeebyId.setStatus(1);
//                employeeService.updateById(employeebyId);
//            }
//        }
        //status已经给了这里不需要自己弄
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息");
        Employee employeebyId = employeeService.getById(id);
        if (employeebyId==null){
            return R.error("没有查询到员工信息");
        }
        return R.success(employeebyId);
    }
}
