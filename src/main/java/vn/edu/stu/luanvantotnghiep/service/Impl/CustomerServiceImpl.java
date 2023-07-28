package vn.edu.stu.luanvantotnghiep.service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import vn.edu.stu.luanvantotnghiep.model.Customer;
import vn.edu.stu.luanvantotnghiep.model.Province;
import vn.edu.stu.luanvantotnghiep.model.Role;
import vn.edu.stu.luanvantotnghiep.repository.CustomerRepository;
import vn.edu.stu.luanvantotnghiep.repository.ProvinceRepository;
import vn.edu.stu.luanvantotnghiep.repository.RoleRepository;
import vn.edu.stu.luanvantotnghiep.service.ICustomerService;

@Service
public class CustomerServiceImpl implements ICustomerService{
    @Autowired
    private CustomerRepository gCustomerRepository;
    @Autowired
    private RoleRepository gRoleRepository;
    @Autowired
    private ProvinceRepository gProvinceRepository;

    public List<Customer> findAllCustmer() {
        List<Customer> lstCustomer = new ArrayList<Customer>();
        gCustomerRepository.findCustomerActivated().forEach(lstCustomer::add);
        if (lstCustomer.size() != 0)
            return lstCustomer;
        return null;
    }

    public Customer findCustomerByUsername(String username) {
        Customer cus = gCustomerRepository.findByUsername(username);
        if (cus != null)
            return cus;
        return null;
    }

    public Customer findCustomerById(int id) {
        Optional<Customer> customer = gCustomerRepository.findCustomerByIdAndActivated(id);
        if (customer.isPresent())
            return customer.get();
        return null;
    }

    public Customer createCusomer(Customer customer) {
        Role roleCustomer = new Role();
        roleCustomer = gRoleRepository.findByRoleKey("ROLE_CUSTOMER");
        Customer newCus = new Customer();
        newCus.setHoTenLot(customer.getHoTenLot());
        newCus.setTen(customer.getTen());
        newCus.setNgaySinh(customer.getNgaySinh());
        newCus.setSoDienThoai(customer.getSoDienThoai());
        newCus.setEmail(customer.getEmail());
        newCus.setDiaChi(customer.getDiaChi());
        newCus.setUsername(customer.getUsername());
        newCus.setPassword(new BCryptPasswordEncoder().encode(customer.getPassword()));
        newCus.setRole(roleCustomer);
        newCus.setCreatedAt(new Date());
        newCus.setProvince(customer.getProvince());
        newCus.setActive(1);
        Customer resultCus = gCustomerRepository.findByUsername(customer.getUsername());
        if (resultCus != null) {
            return null;
        } else {
            return gCustomerRepository.save(newCus);
        }
    }

    public Customer createAdmin(Customer customer) {
        Role roleCustomer = new Role();
        roleCustomer = gRoleRepository.findByRoleKey("ROLE_ADMIN");
        Customer newCus = new Customer();
        Optional<Province> province = gProvinceRepository.findById(customer.getProvince().getId());
        newCus.setHoTenLot(customer.getHoTenLot());
        newCus.setTen(customer.getTen());
        newCus.setNgaySinh(customer.getNgaySinh());
        newCus.setSoDienThoai(customer.getSoDienThoai());
        newCus.setEmail(customer.getEmail());
        newCus.setDiaChi(customer.getDiaChi());
        newCus.setUsername(customer.getUsername());
        newCus.setPassword(new BCryptPasswordEncoder().encode(customer.getPassword()));
        newCus.setProvince(province.get());
        newCus.setRole(roleCustomer);
        newCus.setCreatedAt(new Date());
        newCus.setActive(1);
        Customer resultCus = gCustomerRepository.findByUsername(customer.getUsername());
        if (resultCus != null) {
            return null;
        } else {
            return gCustomerRepository.save(newCus);
        }
    }

    public Customer updateCusomer(Customer customer, int id) {
        Optional<Customer> resultCus = gCustomerRepository.findById(id);
        if (resultCus.isPresent()) {
            Customer newCus = resultCus.get();
            newCus.setHoTenLot(customer.getHoTenLot());
            newCus.setTen(customer.getTen());
            newCus.setNgaySinh(customer.getNgaySinh());
            newCus.setSoDienThoai(customer.getSoDienThoai());
            newCus.setEmail(customer.getEmail());
            newCus.setDiaChi(customer.getDiaChi());
            newCus.setProvince(customer.getProvince());
            newCus.setCreatedAt(new Date());
            newCus.setActive(1);
            return gCustomerRepository.save(newCus);
        }else{
            return null;
        }

    }
    public Customer deleteCustomer(int id){
        Optional<Customer> resultCustomers = gCustomerRepository.findById(id);
        if(resultCustomers.isPresent()){
             resultCustomers.get().setActive(0);
             return gCustomerRepository.save(resultCustomers.get());
             
        }else{
            return null;
        }
    }
    public Integer countCus(){
        return (int) gCustomerRepository.count();
    }

    @Override
    public Customer findByUsernameAndEmail(String username, String email) {
        // TODO Auto-generated method stub
        return gCustomerRepository.findByUsernameAndEmail(username, email);
    }

    @Override
    public Customer updatePassword(Customer customer) {
        // TODO Auto-generated method stub
        return gCustomerRepository.save(customer);
    }

    @Override
    public List<Customer> findAll() {
        // TODO Auto-generated method stub
        return gCustomerRepository.findAll();
    }
}
