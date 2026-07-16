package com.mycompany.techstore.services;

import java.util.List;

import com.mycompany.techstore.Exceptions.ProfileException;
import com.mycompany.techstore.Models.Objects.Address;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.AddressRepository;
import com.mycompany.techstore.Repositories.ProfileRepository;

public class ProfileService {

    private final String emailFormat = "(?i)^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
    private final String nameFormat = "^[\\p{L}\\s\\-\\u0027. ]+$";
    private final String phoneFormat = "[+0-9]+";
    private final String generalAddressFormat = "^[\\p{L}\\s\\-\\u0027. /0-9]+$";

    private final ProfileRepository profileRepo;
    private final AddressRepository addressRepo;
    private final AuthService authService;

    public ProfileService() {
        this.profileRepo = new ProfileRepository();
        this.addressRepo = new AddressRepository();
        this.authService = new AuthService();
    }

    // Update profile fields and return refreshed user or null on failure
    public User UpdateProfile(String email, String fullName, String phone) throws ProfileException {
        if (!email.matches(this.emailFormat)) {
            throw new ProfileException(-1, "Email is not in correct format");
        }

        if (!fullName.matches(this.nameFormat)) {
            throw new ProfileException(-1, "Full name is not in correct format");
        }

        if (!phone.matches(this.phoneFormat)) {
            throw new ProfileException(-1, "Phone number is not in correct format");
        }

        boolean status = this.profileRepo.UpdateProfileByEmail(email, fullName, phone);
        if (!status) {
            return null;
        }

        // Refresh user state from auth service
        return this.authService.GetUserByEmail(email);
    }

    // Addresses
    public List<Address> GetAddressesForUser(int userId) {
        return this.addressRepo.GetAddressesByUserId(userId);
    }

    public boolean CreateAddress(int userId, String homeAddress, String phone, String province, String ward, boolean isDefault) throws ProfileException {
        if (!homeAddress.matches(this.generalAddressFormat)
                || !province.matches(this.generalAddressFormat)
                || !ward.matches(this.generalAddressFormat)) {
            throw new ProfileException(-1, "Address is not in correct format");
        }

        if (!phone.matches(this.phoneFormat)) {
            throw new ProfileException(-1, "Phone is not in correct format");
        }

        return this.addressRepo.CreateAddress(userId, homeAddress, phone, province, ward, isDefault);
    }

    public boolean UpdateAddress(int userId, int addressId, String homeAddress, String phone, String province, String ward, boolean isDefault) throws ProfileException {
        if (!homeAddress.matches(this.generalAddressFormat)
                || !province.matches(this.generalAddressFormat)
                || !ward.matches(this.generalAddressFormat)) {
            throw new ProfileException(-1, "Address is not in correct format");
        }

        if (!phone.matches(this.phoneFormat)) {
            throw new ProfileException(-1, "Phone is not in correct format");
        }

        return this.addressRepo.UpdateAddress(userId, addressId, homeAddress, phone, province, ward, isDefault);
    }

    public boolean DeleteAddress(int addressId, int userId) {
        return this.addressRepo.DeleteAddress(addressId, userId);
    }
}
