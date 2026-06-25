package com.mycompany.techstore.services;

import java.util.List;

import com.mycompany.techstore.Exceptions.AuthException;
import com.mycompany.techstore.Models.Objects.Address;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.AddressRepository;
import com.mycompany.techstore.Repositories.ProfileRepository;

public class ProfileService {

    private final ProfileRepository profileRepo;
    private final AddressRepository addressRepo;
    private final AuthService authService;

    public ProfileService() {
        this.profileRepo = new ProfileRepository();
        this.addressRepo = new AddressRepository();
        this.authService = new AuthService();
    }

    // Update profile fields and return refreshed user or null on failure
    public User UpdateProfile(String email, String fullName, String phone) throws AuthException {
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

    public boolean CreateAddress(int userId, String homeAddress, String phone, String province, String ward, boolean isDefault) {
        return this.addressRepo.CreateAddress(userId, homeAddress, phone, province, ward, isDefault);
    }

    public boolean UpdateAddress(int userId, int addressId, String homeAddress, String phone, String province, String ward, boolean isDefault) {
        return this.addressRepo.UpdateAddress(userId, addressId, homeAddress, phone, province, ward, isDefault);
    }

    public boolean DeleteAddress(int addressId, int userId) {
        return this.addressRepo.DeleteAddress(addressId, userId);
    }
}
