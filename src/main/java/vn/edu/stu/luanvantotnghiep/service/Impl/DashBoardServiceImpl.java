package vn.edu.stu.luanvantotnghiep.service.Impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.stu.luanvantotnghiep.model.DashBoard;
import vn.edu.stu.luanvantotnghiep.repository.CustomerRepository;
import vn.edu.stu.luanvantotnghiep.repository.DashBoardRepository;
import vn.edu.stu.luanvantotnghiep.repository.HoaDonRepository;
import vn.edu.stu.luanvantotnghiep.repository.KhuyenMaiRepository;
import vn.edu.stu.luanvantotnghiep.repository.LoaiSanPhamRepository;
import vn.edu.stu.luanvantotnghiep.repository.NhaCungCapRepository;
import vn.edu.stu.luanvantotnghiep.repository.NhaSanXuatRepository;
import vn.edu.stu.luanvantotnghiep.repository.SanPhamRepository;
import vn.edu.stu.luanvantotnghiep.service.ICustomerService;
import vn.edu.stu.luanvantotnghiep.service.IDashBoardService;
import vn.edu.stu.luanvantotnghiep.service.IHoaDonService;
import vn.edu.stu.luanvantotnghiep.service.IKhuyenMaiService;
import vn.edu.stu.luanvantotnghiep.service.ILoaiSanPhamService;
import vn.edu.stu.luanvantotnghiep.service.INhaCungCapService;
import vn.edu.stu.luanvantotnghiep.service.INhaSanXuatService;
import vn.edu.stu.luanvantotnghiep.service.ISanPhamService;

@Service
public class DashBoardServiceImpl implements IDashBoardService{
    @Autowired
    private DashBoardRepository dashBoardRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;
    @Autowired
    private NhaSanXuatRepository nhaSanXuatRepository;
    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;
    @Autowired
    private ICustomerService customerService;
    @Autowired
    private ISanPhamService sanPhamService;
    @Autowired
    private IHoaDonService hoaDonService;
    @Autowired
    private ILoaiSanPhamService loaiSanPhamService;
    @Autowired
    private IKhuyenMaiService khuyenMaiService;
    @Autowired
    private INhaCungCapService nhaCungCapService;
    @Autowired
    private INhaSanXuatService nhaSanXuatService;
    @Override
    public DashBoard create(DashBoard dashBoard) {
        // TODO Auto-generated method stub
        return dashBoardRepository.save(dashBoard);
    }
    @Override
    public DashBoard findByDateBefore() {
        // TODO Auto-generated method stub
        Calendar cal = Calendar.getInstance();
        int date = cal.get(Calendar.DATE) - 1;
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        cal.set(year, month, date);
        return dashBoardRepository.findByDateCreateBetween(cal.getTime(), Calendar.getInstance().getTime());
    }
    @Override
    public DashBoard getValueInDateNow(){
        // TODO Auto-generated method stub
        Integer active = 1;
        Calendar cal = Calendar.getInstance();
        // int date = cal.get(Calendar.DATE) -1;
        // cal.set(Calendar.YEAR, Calendar.MONTH + 1, date);
        Date createDate = cal.getTime();
        Integer cus = customerRepository.countByCreatedAtAndActive(createDate, active);
        Integer sp = sanPhamRepository.countByTrangThaiAndCreateDate(active, createDate);
        Integer loaiSP = loaiSanPhamRepository.countByActiveAndCreateDate(active, createDate);
        Integer ncc = nhaCungCapRepository.countByActiveAndCreateDate(active, createDate);
        Integer nsx = nhaSanXuatRepository.countByActiveAndCreateDate(active, createDate);
        Integer km = khuyenMaiRepository.countByActiveAndCreateDate(active, createDate);
        Integer hd = hoaDonRepository.countByCreateDate(createDate);
        DashBoard data = findByDateBefore();
        System.out.println(Calendar.getInstance().getTime());
        System.out.println("So Nhan Vien ngay hom nay" + cus);
        data.setCountUser(data.getCountUser() + cus);
        data.setCountKhuyenMai(data.getCountKhuyenMai() + km);
        data.setCountDonHang(data.getCountDonHang() + hd);
        data.setCountLoaiSanPham(data.getCountLoaiSanPham() + loaiSP);
        data.setCountSanPham(data.getCountSanPham() + sp);
        data.setCountNhaCungCap(data.getCountNhaCungCap() + ncc);
        data.setCountNhaSanXuat(data.getCountNhaSanXuat() + nsx);
        return data;
    }
    @Override
    public DashBoard scheduleEveryDay() {
        Integer countUser = customerService.countCus();
        Integer countDonHang = hoaDonService.countHoaDon();
        Integer countSanPham = sanPhamService.countSanPham();
        Integer countLoaiSanPham = loaiSanPhamService.countLoaiSanPham();
        Integer countKhuyenMai = khuyenMaiService.countKhuyenMai();
        Integer countNhaCungCap = nhaCungCapService.countNhaCungCap();
        Integer countNhaSanXuat = nhaSanXuatService.countNhaSanXuat();
        Date dateCreate = Calendar.getInstance().getTime();
        String trangThai = "Complete";
        DashBoard dashBoard = new DashBoard(countUser, countDonHang, countSanPham, countLoaiSanPham, countKhuyenMai, countNhaCungCap, countNhaSanXuat, dateCreate, trangThai);
        return dashBoardRepository.save(dashBoard);
    }
    
}
