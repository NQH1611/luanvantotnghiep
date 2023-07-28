package vn.edu.stu.luanvantotnghiep.controller;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.edu.stu.luanvantotnghiep.model.Customer;
import vn.edu.stu.luanvantotnghiep.model.District;
import vn.edu.stu.luanvantotnghiep.model.FormatApi;
import vn.edu.stu.luanvantotnghiep.model.ModelSendMail;
import vn.edu.stu.luanvantotnghiep.model.ModelUser;
import vn.edu.stu.luanvantotnghiep.model.Province;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.edu.stu.luanvantotnghiep.service.ICustomerService;
import vn.edu.stu.luanvantotnghiep.service.IDistrictService;
import vn.edu.stu.luanvantotnghiep.service.IProvinceService;
import vn.edu.stu.luanvantotnghiep.service.ISendMailService;
import vn.edu.stu.luanvantotnghiep.service.IWardService;
import vn.edu.stu.luanvantotnghiep.service.CustomerLogin;
import vn.edu.stu.luanvantotnghiep.service.TokenService;
import vn.edu.stu.luanvantotnghiep.security.UserPrincipal;
import vn.edu.stu.luanvantotnghiep.model.Token;
import vn.edu.stu.luanvantotnghiep.model.Ward;
import vn.edu.stu.luanvantotnghiep.security.JwtUtil;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/")
public class CustomerController {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 20;
    @Autowired
    private ICustomerService gCustomerService;
    @Autowired
    private CustomerLogin gCustomerLogin;
    @Autowired
    private TokenService gTokenService;
    @Autowired
    private IProvinceService provinceService;
    @Autowired
    private IDistrictService districtService;
    @Autowired
    private IWardService wardService;
    @Autowired
    private ISendMailService sendMailService;

    @GetMapping("/customer")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<Customer>> getAllCustomer(){
        List<Customer> lstCus = gCustomerService.findAllCustmer();
        if(lstCus != null) return new ResponseEntity<>(lstCus, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
    @GetMapping("/customerall")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<Customer>> getCustomer(){
        List<Customer> lstCus = gCustomerService.findAll();
        if(lstCus != null) return new ResponseEntity<>(lstCus, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("id") int id){
        Customer cus = gCustomerService.findCustomerById(id);
        if(cus != null) return new ResponseEntity<>(cus, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
    @PostMapping("/customer/register")
    public ResponseEntity<?> createCustomer( @RequestBody ModelUser customer){
        try {
            Province thanhPho = provinceService.findById(customer.getProvince()).get();
            District huyen = districtService.findById(customer.getDistrict()).get();
            Ward xa = wardService.findById(customer.getWard()).get();
            Customer data = new Customer();
            data.setHoTenLot(customer.getHoTenLot());
            data.setEmail(customer.getEmail());
            data.setTen(customer.getTen());
            data.setUsername(customer.getUsername());
            data.setPassword(customer.getPassword());
            data.setDiaChi(customer.getDiaChi() + "," + xa.getName() + "," + huyen.getName() + "," + thanhPho.getName());
            data.setNgaySinh(customer.getNgaySinh());
            data.setSoDienThoai(customer.getSoDienThoai());
            data.setProvince(thanhPho);
            Customer saveCus = gCustomerService.createCusomer(data);
            if(saveCus == null) return new ResponseEntity<>("Username của bạn đã tồn tại!", HttpStatus.PARTIAL_CONTENT);
            return new ResponseEntity<>(saveCus, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity()
                    .body("Failed to Create specified Customer: " + e.getCause().getCause().getMessage());
        }
    }
    @GetMapping("/getdatatotoken")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public FormatApi getUserByToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            FormatApi result = new FormatApi();
            result.setMessage("No Authentication user not found!");
            result.setStatus(HttpStatus.NOT_FOUND);
            return result;
        }
        Customer cusResult = gCustomerService.findCustomerByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if(cusResult !=null){
            FormatApi result = new FormatApi();
            result.setData(cusResult);
            result.setMessage("Thành công!");
            result.setStatus(HttpStatus.OK);
            return result;
        }else{
            FormatApi result = new FormatApi();
            result.setData(cusResult);
            result.setMessage("Không có dữ liệu user!");
            result.setStatus(HttpStatus.NOT_FOUND);
            return result;
        }
        
    }
    @PostMapping("/admin/register")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> createAdmin( @RequestBody ModelUser customer){
        try {
            Province thanhPho = provinceService.findById(customer.getProvince()).get();
            District huyen = districtService.findById(customer.getDistrict()).get();
            Ward xa = wardService.findById(customer.getWard()).get();
            Customer data = new Customer();
            data.setHoTenLot(customer.getHoTenLot());
            data.setEmail(customer.getEmail());
            data.setTen(customer.getTen());
            data.setUsername(customer.getUsername());
            data.setPassword(customer.getPassword());
            data.setDiaChi(customer.getDiaChi() + "," + xa.getName() + "," + huyen.getName() + "," + thanhPho.getName());
            data.setNgaySinh(customer.getNgaySinh());
            data.setSoDienThoai(customer.getSoDienThoai());
            data.setProvince(thanhPho);
            Customer saveCus = gCustomerService.createAdmin(data);
            if(saveCus == null) return new ResponseEntity<>("Username của bạn đã tồn tại!", HttpStatus.PARTIAL_CONTENT);
            return new ResponseEntity<>(saveCus, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity()
                    .body("Failed to Create specified Admin: " + e.getCause().getCause().getMessage());
        }
    }

    @PostMapping("/customer/login")
    public ResponseEntity<?> login(@RequestBody UserPrincipal user) {
        Customer customer = gCustomerService.findCustomerByUsername(user.getUsername());
        if(customer != null && customer.getActive() == 1){
            UserPrincipal userPrincipal = gCustomerLogin.findByUsername(user.getUsername());
            if (new BCryptPasswordEncoder().matches(user.getPassword(), userPrincipal.getPassword()) == false) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("tài khoản hoặc mật khẩu không chính xác");
            }
            Token token = new Token();
            token.setToken(JwtUtil.generateToken(userPrincipal));
            token.setTokenExpDate(JwtUtil.generateExpirationDate());
            token.setCreatedBy(userPrincipal.getUserId());
            gTokenService.createToken(token);
            return ResponseEntity.ok(token.getToken());
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("tài khoản của bạn không tồn tại");
        }
       
    }
    @PutMapping("/customer/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public ResponseEntity<?> updateCustomer(@RequestBody Customer customer, @PathVariable("id") int id){
        try {
            Customer resultCus = gCustomerService.updateCusomer(customer, id);
            if(resultCus != null) return new ResponseEntity<>(resultCus, HttpStatus.OK);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity()
                    .body("Failed to Update specified Customer: " + e.getCause().getCause().getMessage());
        }
    }

    @DeleteMapping("customer/{id}/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<FormatApi> deleteCustomer(@PathVariable("id") int id){
        try {
            Customer saveCustomer = gCustomerService.deleteCustomer(id);
            if(saveCustomer != null) return new ResponseEntity<>(new FormatApi(HttpStatus.ACCEPTED, "Thành công", saveCustomer), HttpStatus.ACCEPTED);
            return new ResponseEntity<>(new FormatApi(HttpStatus.NOT_FOUND, "Không thành công", saveCustomer), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new FormatApi(HttpStatus.NOT_FOUND, "Failed to Delete specified Employee: " + e.getCause().getCause().getMessage(), e), HttpStatus.NOT_FOUND); 
        }
    }
    @PostMapping("/forgotpassword")
    public FormatApi forgotPassword(@RequestBody ModelSendMail sendMail){
        Customer customer = gCustomerService.findByUsernameAndEmail(sendMail.getUsername(), sendMail.getMail());
        if(customer == null){
            return new FormatApi(HttpStatus.NOT_FOUND, "Không tìm thấy username hoặc email của bạn", null);
        }else{
            String newPassword = generateRandomPassword();
            customer.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            gCustomerService.updatePassword(customer);
            String body = "Mật khẩu mới của bạn là: " + newPassword + "<br><br>"
            + "Vui lòng nhấp vào nút bên dưới để đặt lại mật khẩu:<br>"
            + "<a href=\"" + sendMail.getDiachi() + "\">"
            + "<button style=\"background-color:#008CBA; color:white; padding: 10px 20px; border:none; border-radius: 5px;\">Đặt lại mật khẩu</button>"
            + "</a>";
            String subject = "Đây là mail cấp mật khẩu mới cho bạn!";
            sendMailService.sendMail(sendMail.getMail(), subject, body);
            return new FormatApi(HttpStatus.OK, "Hãy kiểm tra email của bạn nhé!", null);
        }
        
    }
    public static String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }
    @PostMapping("/resetpassword")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public FormatApi resetPassword(@RequestParam String newPassword){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            FormatApi result = new FormatApi();
            result.setMessage("No Authentication user not found!");
            result.setStatus(HttpStatus.NOT_FOUND);
            return result;
        }
        Customer cusResult = gCustomerService.findCustomerByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        cusResult.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        cusResult = gCustomerService.updatePassword(cusResult);
        return new FormatApi(HttpStatus.OK, "Cập nhật password thành công", cusResult);
        
        
    }
}
