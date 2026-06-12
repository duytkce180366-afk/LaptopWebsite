package com.mycompany.techstore.services;

import com.mycompany.techstore.Exceptions.AuthException;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.ProfileRepository;

public class ProfileService {

	private final ProfileRepository profileRepo;
	private final AuthService authService;

	public ProfileService() {
		this.profileRepo = new ProfileRepository();
		this.authService = new AuthService();
	}

	// Update profile fields and return refreshed user or null on failure
	public User UpdateProfile(String email, String fullName, String phone) throws AuthException {
		boolean ok = this.profileRepo.UpdateProfileByEmail(email, fullName, phone);
		if (!ok) {
			return null;
		}

		// Refresh user state from auth service
		return this.authService.GetUserByEmail(email);
	}

}
