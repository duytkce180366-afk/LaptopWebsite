package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.Voucher;
import com.mycompany.techstore.Repositories.VoucherRepository;
import java.util.Date;

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
}
