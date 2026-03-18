package lk.ijse.eca.userservice.mapper;

import lk.ijse.eca.userservice.dto.UserRequestDTO;
import lk.ijse.eca.userservice.dto.UserResponseDTO;
import lk.ijse.eca.userservice.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class UserMapper {

    @Mapping(target = "picture", expression = "java(buildPictureUrl(user))")
    public abstract UserResponseDTO toResponseDto(User user);

    @Mapping(target = "picture", ignore = true)
    public abstract User toEntity(UserRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "nic", ignore = true)
    @Mapping(target = "picture", ignore = true)
    public abstract void updateEntity(UserRequestDTO dto, @MappingTarget User user);

    protected String buildPictureUrl(User user) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/users/{nic}/picture")
                .buildAndExpand(user.getNic())
                .toUriString();
    }
}
