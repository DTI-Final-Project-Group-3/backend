package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.UserAddress;
import com.warehub.warehub.infrastructure.users.dto.UserAddressRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserAddressResponseDTO;
import com.warehub.warehub.infrastructure.users.dto.UserAuth;
import com.warehub.warehub.infrastructure.users.repository.UserAddressRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.UserAddressUsecase;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAddressUsecaseImpl implements UserAddressUsecase {
    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UsersRepository usersRepository;

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public List<UserAddressResponseDTO> getAll() {
        User user = UserAuth.getCurrentUser(usersRepository);
        List<UserAddress> addresses = userAddressRepository.findByUserId(user.getId());

        return addresses.stream().map(this::mapToResponse).toList();
    }

    @Override
    public UserAddressResponseDTO getMain() {
        User user = UserAuth.getCurrentUser(usersRepository);
        return userAddressRepository.findByUserIdAndIsPrimaryTrue(user.getId())
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Override
    public UserAddressResponseDTO read(Long id) {
        User user = UserAuth.getCurrentUser(usersRepository);
        UserAddress address = userAddressRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElse(null);

        return mapToResponse(address);
    }

    @Override
    public UserAddressResponseDTO create(UserAddressRequestDTO request) {
        User user = UserAuth.getCurrentUser(usersRepository);
        Long userId = user.getId();

        List<UserAddress> existingAddresses = userAddressRepository.findByUserId(userId);

        UserAddress userAddress = new UserAddress();
        userAddress.setName(request.getName());
        userAddress.setDetailAddress(request.getDetailAddress());
        userAddress.setLocation(geometryFactory.createPoint(
                new Coordinate(request.getLongitude(), request.getLatitude())));

        if (existingAddresses.isEmpty()) {
            userAddress.setPrimary(true);
        } else {
            userAddress.setPrimary(false);
        }

        userAddress.setUser(user);

        UserAddress savedUserAddress = userAddressRepository.save(userAddress);
        UserAddressResponseDTO response = new UserAddressResponseDTO();

        response.setId(savedUserAddress.getId());
        response.setName(savedUserAddress.getName());
        response.setDetailAddress(savedUserAddress.getDetailAddress());
        response.setPrimary(savedUserAddress.isPrimary());
        Point location = savedUserAddress.getLocation();
        response.setLatitude(location.getX());
        response.setLongitude(location.getY());
        response.setCreatedAt(savedUserAddress.getCreatedAt());
        response.setUpdatedAt(savedUserAddress.getUpdatedAt());

        return response;
    }

    @Override
    public UserAddressResponseDTO update(Long id, UserAddressRequestDTO request) {
        User user = UserAuth.getCurrentUser(usersRepository);
        UserAddress address = userAddressRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (address == null)
            return null;

        address.setName(request.getName());
        address.setDetailAddress(request.getDetailAddress());
        address.setLocation(geometryFactory.createPoint(
                new Coordinate(request.getLongitude(), request.getLatitude())));

        if (request.getIsPrimary()) {
            userAddressRepository.unsetOtherPrimaryAddresses(user.getId());
            address.setPrimary(true);
        }

        UserAddress updatedAddress = userAddressRepository.save(address);
        return mapToResponse(updatedAddress);
    }

    @Override
    public UserAddressResponseDTO delete(Long id) {
        User user = UserAuth.getCurrentUser(usersRepository);
        UserAddress address = userAddressRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (address == null)
            return null;

        boolean wasPrimary = address.isPrimary();
        userAddressRepository.delete(address);

        // If deleted address was primary, set another address as primary
        if (wasPrimary) {
            userAddressRepository.findByUserId(user.getId()).stream().findFirst().ifPresent(a -> {
                a.setPrimary(true);
                userAddressRepository.save(a);
            });
        }

        return mapToResponse(address);
    }

    private UserAddressResponseDTO mapToResponse(UserAddress address) {
        if (address == null)
            return null;
        UserAddressResponseDTO response = new UserAddressResponseDTO();
        response.setId(address.getId());
        response.setName(address.getName());
        response.setDetailAddress(address.getDetailAddress());
        response.setPrimary(address.isPrimary());
        Point location = address.getLocation();
        response.setLatitude(location.getX());
        response.setLongitude(location.getY());
        response.setCreatedAt(address.getCreatedAt());
        response.setUpdatedAt(address.getUpdatedAt());
        return response;
    }
}
