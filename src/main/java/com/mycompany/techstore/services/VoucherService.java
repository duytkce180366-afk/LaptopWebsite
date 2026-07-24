package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.Voucher;
import com.mycompany.techstore.Repositories.VoucherRepository;
import java.util.Date;
import java.util.List;

public class VoucherService {

    private VoucherRepository repo
            = new VoucherRepository();

    public Voucher validateVoucher(
            String code) {
        Voucher voucher
                = repo.getByCode(code);
        if (voucher == null) {
            return null;
        }
        if (!voucher.getStatus()
                .equalsIgnoreCase(
                        "Active")) {
            return null;
        }
        if (voucher.getQuantity() <= 0) {
            return null;
        }
        if (voucher.getExpiredDate()
                .before(new Date())) {
            return null;
        }
        return voucher;
    }

    // Decrease the voucher's remaining usage count by 1
    public boolean decreaseQuantity(int voucherId) {
        return repo.decreaseQuantity(voucherId);
    }

    public List<Voucher> getAllVoucher() {

        return repo.getAll();

    }

    public boolean createVoucher(Voucher voucher) {

        return repo.createVoucher(voucher);

    }

    public Voucher getVoucherById(int id) {
        return repo.getById(id);
    }

    public boolean updateVoucher(Voucher voucher) {
        return repo.updateVoucher(voucher);
    }

    public boolean deleteVoucher(int id) {
        return repo.deleteVoucher(id);
    }

    public List<Voucher> filterVoucher(
            String keyword,
            String status,
            String discountPercent,
            String expiredDate) {

        return repo.filterVoucher(
                keyword,
                status,
                discountPercent,
                expiredDate);
    }

    public void updateExpiredVoucher() {
        repo.updateExpiredVoucher();
    }
}
