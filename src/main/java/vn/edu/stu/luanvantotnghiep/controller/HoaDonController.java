package vn.edu.stu.luanvantotnghiep.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.stu.luanvantotnghiep.model.ChiTietHoaDon;
import vn.edu.stu.luanvantotnghiep.model.Customer;
import vn.edu.stu.luanvantotnghiep.model.FormatApi;
import vn.edu.stu.luanvantotnghiep.model.HoaDon;
import vn.edu.stu.luanvantotnghiep.model.ModelHoaDon;
import vn.edu.stu.luanvantotnghiep.model.PhieuBaoHanh;
import vn.edu.stu.luanvantotnghiep.model.SanPham;
import vn.edu.stu.luanvantotnghiep.model.TraGop;
import vn.edu.stu.luanvantotnghiep.service.IChiTietHoaDonService;
import vn.edu.stu.luanvantotnghiep.service.ICustomerService;
import vn.edu.stu.luanvantotnghiep.service.IHoaDonService;
import vn.edu.stu.luanvantotnghiep.service.IPhieuBaoHanhService;
import vn.edu.stu.luanvantotnghiep.service.ISanPhamService;
import vn.edu.stu.luanvantotnghiep.service.ITraGopService;

@RestController
@CrossOrigin(maxAge = 3600)
public class HoaDonController {
    @Autowired
    private IHoaDonService hoaDonService;
    @Autowired
    private ISanPhamService sanPhamRepository;
    @Autowired
    private ICustomerService customerRepository;
    @Autowired
    private IChiTietHoaDonService chiTietHoaDonService;
    @Autowired
    private ITraGopService traGopRepository;
    @Autowired
    private IPhieuBaoHanhService phieuBaoHanhService;
    @GetMapping("/hoadon")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public FormatApi findAllHoaDon(){
        List<HoaDon> lst = hoaDonService.findAll();
        if(!lst.isEmpty()){
            FormatApi format = new FormatApi(HttpStatus.OK, "Thành công", lst);
            return format;
        }else{
            FormatApi format = new FormatApi(HttpStatus.NO_CONTENT, "Không có dữ liệu hóa đơn!", lst);
            return format;
        }
    }
    @GetMapping("/hoadon/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public FormatApi findHoaDonByID(@PathVariable("id") Integer id) {
        Optional<HoaDon> data = hoaDonService.findById(id);
        if (data.isPresent()) {
            FormatApi result = new FormatApi();
            result.setData(data);
            result.setMessage("Thành công!");
            result.setStatus(HttpStatus.OK);
            return result;
        } else {
            FormatApi result = new FormatApi();
            result.setData(data);
            result.setMessage("Không có dữ liệu cho bài viết có id = " + id);
            result.setStatus(HttpStatus.NO_CONTENT);
            return result;
        }
    }
    @PostMapping("/hoadon")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public FormatApi createHoaDon(@RequestBody ModelHoaDon hoaDon){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            FormatApi result = new FormatApi();
            result.setMessage("No Authentication user not found!");
            result.setStatus(HttpStatus.NOT_FOUND);
            return result;
        }
        Customer cusResult = customerRepository.findCustomerByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        for(ChiTietHoaDon c : hoaDon.getChiTietHoaDons()){
            SanPham sanPham = sanPhamRepository.findById(c.getSanPham().getId()).get();
            if(sanPham.getSoLuongTon() == 0){
                FormatApi formatApi = new FormatApi(HttpStatus.NO_CONTENT, "Số lượng sản phẩm bạn muốn đặt đã hết, bạn hãy chọn sản phẩm khác nhé!", null);
                return formatApi;
            }else if(sanPham.getSoLuongTon() < c.getSoLuong()){
                FormatApi formatApi = new FormatApi(HttpStatus.OK, "Số lượng sản phẩm chỉ còn: " + sanPham.getSoLuongTon() + ". Bạn hãy chọn lại số lượng!", null);
                return formatApi;
            }
        }
        HoaDon save = new HoaDon();
        save.setCreateDate(Calendar.getInstance().getTime());
        save.setDiaChi(hoaDon.getDiaChi());
        save.setGhiChu(hoaDon.getGhiChu());
        save.setTenKhachHang(hoaDon.getTenKhachHang());
        save.setSoDienThoai(hoaDon.getSoDienThoai());
        save.setTrangThai(1);
        save.setTrangThaiThanhToan(0);
        save.setSoTienTraGop(hoaDon.getSoTienTraGop());
        save.setSoTienTraTruoc(hoaDon.getSoTienTraTruoc());
        if(hoaDon.getSoThangTraGop() > 0){
            save.setSoThangTraGop(hoaDon.getSoThangTraGop());
            save.setIsTraGop(true);
        }else{
            save.setSoThangTraGop(0);
            save.setIsTraGop(false);
        }
        Double tongTien = 0.0;
        for(ChiTietHoaDon c: hoaDon.getChiTietHoaDons()){
            tongTien += c.getGia() * c.getSoLuong();
        }
        save.setTongTien(tongTien);
        save = hoaDonService.create(save);
        int soThangTangDan = 1;
        Double soTienHangThang = (save.getSoTienTraGop() + (save.getSoTienTraGop() * 0.1)) / hoaDon.getSoThangTraGop();
        if(save.getSoThangTraGop() > 0){
            for(int i = 0; i < hoaDon.getSoThangTraGop(); i++){
                TraGop traGop = new TraGop();
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                int year = cal1.get(Calendar.YEAR);
                int month = cal1.get(Calendar.MONTH) + 1;
                int day1 = 5;
                int day2 = 10;
                cal1.set(year, month + soThangTangDan, day1);
                cal2.set(year, month + soThangTangDan, day2);
                soThangTangDan ++;
                traGop.setDongTienTuNgay(cal1.getTime());
                traGop.setDongTienDenNgay(cal2.getTime());
                traGop.setDaBank(false);
                traGop.setHoaDon(save);
                traGop.setTrangThaiPhiPhat(0);
                traGop.setSoTienHangThang(soTienHangThang);
                traGop = traGopRepository.create(traGop);
            }
        }
        if(cusResult.getRole().getId() == 1){
            save.setQuanLy(cusResult);
        }else{
            save.setUser(cusResult);
        }
        for(ChiTietHoaDon c : hoaDon.getChiTietHoaDons()){
            SanPham sanPham = sanPhamRepository.findById(c.getSanPham().getId()).get();
            c.setHoaDon(save);
            c.setSanPham(sanPham);
            c = chiTietHoaDonService.create(c);
            sanPham.setSoLuongTon(sanPham.getSoLuongTon() - c.getSoLuong());
            sanPhamRepository.create(sanPham);
        }
        if(save != null){
            FormatApi formatApi = new FormatApi(HttpStatus.OK, "Tạo hóa đơn thành công!", save);
            return formatApi;
        }else{
            FormatApi formatApi = new FormatApi(HttpStatus.NOT_FOUND, "Tạo hóa đơn không thành công!", save);
            return formatApi;
        }
    }
    @GetMapping("/hoadonbykhachhang")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    public FormatApi findHoaDonByKhachHang(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            FormatApi result = new FormatApi();
            result.setMessage("No Authentication user not found!");
            result.setStatus(HttpStatus.NOT_FOUND);
            return result;
        }
        Customer cusResult = customerRepository.findCustomerByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<HoaDon> lstHoaDon = hoaDonService.findAllHoaDonByKhachHang(cusResult.getId());
        if(!lstHoaDon.isEmpty()){
            FormatApi formatApi = new FormatApi(HttpStatus.OK, "Bạn có hóa đơn", lstHoaDon);
            return formatApi;
        }else{
            FormatApi formatApi = new FormatApi(HttpStatus.NO_CONTENT, "Bạn chưa có hóa đơn nào!", lstHoaDon);
            return formatApi;
        }
    }
    @PutMapping("/hoadon/{id}")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public FormatApi updateHoaDon(@PathVariable("id") Integer id, @RequestBody HoaDon hoaDon){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            FormatApi result = new FormatApi();
            result.setMessage("No Authentication user not found!");
            result.setStatus(HttpStatus.NOT_FOUND);
            return result;
        }
        Customer cusResult = customerRepository.findCustomerByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if(cusResult.getRole().getId() == 1){
            hoaDon.setQuanLy(cusResult);
        }else{
            hoaDon.setUser(cusResult);
        }
        HoaDon result = hoaDonService.update(id, hoaDon);
        if(result == null){
            FormatApi formatApi = new FormatApi(HttpStatus.NO_CONTENT, "Cập nhật không thành công", result);
            return formatApi;
        }else{
            FormatApi formatApi = new FormatApi(HttpStatus.OK, "Cập nhật hóa đơn thành công!", result);
            return formatApi;
        }
    }
    @PutMapping("/hoadon/{id}/chuanbihang")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public FormatApi updateHoaDonChuanBiHang(@PathVariable("id") Integer id){
        HoaDon hoaDon = hoaDonService.updateChuanBiHang(id);
        if(hoaDon == null){
            FormatApi formatApi = new FormatApi(HttpStatus.NO_CONTENT, "Cập nhật trạng thái không thành công", hoaDon);
            return formatApi;
        }else{
            FormatApi formatApi = new FormatApi(HttpStatus.OK, "Cập nhật trạng thái hóa đơn thành công!", hoaDon);
            return formatApi;
        }
    }
    @PutMapping("/hoadon/{id}/giaohang")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public FormatApi updateHoaDonGiaoHang(@PathVariable("id") Integer id){
        HoaDon hoaDon = hoaDonService.updateGiaoHang(id);
        if(hoaDon == null){
            FormatApi formatApi = new FormatApi(HttpStatus.NO_CONTENT, "Cập nhật trạng thái không thành công", hoaDon);
            return formatApi;
        }else{
            FormatApi formatApi = new FormatApi(HttpStatus.OK, "Cập nhật trạng thái hóa đơn thành công!", hoaDon);
            return formatApi;
        }
    }
    @PutMapping("/hoadon/{id}/thanhcong")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public FormatApi updateHoaDonThanhCong(@PathVariable("id") Integer id){
        HoaDon hoaDon = hoaDonService.updateThanhCong(id);
        PhieuBaoHanh phieuBaoHanh = new PhieuBaoHanh();
        phieuBaoHanh.setHoaDon(hoaDon);
        Calendar cal = Calendar.getInstance();
        phieuBaoHanh.setNgayBatDauBaoHanh(cal.getTime());
        int year = cal.get(Calendar.YEAR) + 1;
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH);
        cal.set(year, month, day);
        phieuBaoHanh.setNgayHetBaoHanh(cal.getTime());
        phieuBaoHanh = phieuBaoHanhService.create(phieuBaoHanh);
        if(hoaDon == null){
            FormatApi formatApi = new FormatApi(HttpStatus.NO_CONTENT, "Cập nhật trạng thái không thành công", hoaDon);
            return formatApi;
        }else{
            FormatApi formatApi = new FormatApi(HttpStatus.OK, "Cập nhật trạng thái hóa đơn thành công!", hoaDon);
            return formatApi;
        }
    }
    @PutMapping("/hoadon/{id}/xoa")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public FormatApi updateHoaDonXoa(@PathVariable("id") Integer id){
        HoaDon hoaDon = hoaDonService.updateXoaDonHang(id);
        if(hoaDon == null){
            FormatApi formatApi = new FormatApi(HttpStatus.NO_CONTENT, "Cập nhật trạng thái không thành công", hoaDon);
            return formatApi;
        }else{
            FormatApi formatApi = new FormatApi(HttpStatus.OK, "Cập nhật trạng thái hóa đơn thành công!", hoaDon);
            return formatApi;
        }
    }
    @PutMapping("/hoadon/{id}/thanhtoan")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public FormatApi updateHoaDonThanhToan(@PathVariable("id") Integer id){
        HoaDon hoaDon = hoaDonService.updateDaThanhToan(id);
        if(hoaDon == null){
            FormatApi formatApi = new FormatApi(HttpStatus.NO_CONTENT, "Cập nhật trạng thái không thành công", hoaDon);
            return formatApi;
        }else{
            FormatApi formatApi = new FormatApi(HttpStatus.OK, "Cập nhật trạng thái hóa đơn thành công!", hoaDon);
            return formatApi;
        }
    }
}
