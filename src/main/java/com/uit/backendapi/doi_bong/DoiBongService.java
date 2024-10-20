package com.uit.backendapi.doi_bong;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.uit.backendapi.Utils;
import com.uit.backendapi.doi_bong.dto.CreateDoiBongDto;
import com.uit.backendapi.doi_bong.dto.UpdateDoiBongDto;
import com.uit.backendapi.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class DoiBongService implements IDoiBongService {
    private final DoiBongRepository doiBongRepository;
    private final Cloudinary cloudinary;

    @Override
    public List<DoiBong> getAllDoiBong() {
        return doiBongRepository.findAll();
    }

    @Override
    public DoiBong getDoiBongById(Long id) {
        return doiBongRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Doi bong not found")
        );
    }

    @Override
    public DoiBong createDoiBong(CreateDoiBongDto createDoiBongDto) throws IOException {
        DoiBong doiBong = new DoiBong();
        doiBong.setLogo("");
        doiBong.setAoChinhThuc("");
        doiBong.setAoDuBi("");
        BeanUtils.copyProperties(createDoiBongDto, doiBong);
        doiBong = doiBongRepository.save(doiBong);

        String folder = "doi-bong/" + doiBong.getId();

        String logoUrl = uploadToCloudinary(createDoiBongDto.getLogo().getBytes(), folder, doiBong.getId() + "_logo");
        String aoChinhThucUrl = uploadToCloudinary(createDoiBongDto.getAoChinhThuc().getBytes(), folder, doiBong.getId() + "_ao_chinh_thuc");
        String aoDuBiUrl = uploadToCloudinary(createDoiBongDto.getAoDuBi().getBytes(), folder, doiBong.getId() + "_ao_du_bi");

        doiBong.setLogo(logoUrl);
        doiBong.setAoChinhThuc(aoChinhThucUrl);
        doiBong.setAoDuBi(aoDuBiUrl);

        return doiBongRepository.save(doiBong);
    }

    private String uploadToCloudinary(byte[] fileBytes, String folder, String publicId) throws IOException {
        Map uploadParams = ObjectUtils.asMap(
                "folder", folder,
                "public_id", publicId,
                "use_filename", false,
                "unique_filename", false,
                "overwrite", true
        );

        Map uploadResult = cloudinary.uploader().upload(fileBytes, uploadParams);
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public DoiBong updateDoiBong(Long id, UpdateDoiBongDto updateDoiBongDto) {
        return doiBongRepository.findById(id)
                .map(existingDoiBong -> {
                    try {
                        return updateExistingDoiBong(existingDoiBong, updateDoiBongDto);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(doiBongRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Doi bong not found"));
    }

    private DoiBong updateExistingDoiBong(DoiBong existingDoiBong, UpdateDoiBongDto updateDoiBongDto) throws IOException {

        Utils.copyNonNullProperties(updateDoiBongDto, existingDoiBong, "id", "aoChinhThuc", "aoDuBi", "logo");

        String folder = "doi-bong/" + existingDoiBong.getId();

        if (updateDoiBongDto.getLogo() != null) {
            String logoUrl = uploadToCloudinary(updateDoiBongDto.getLogo().getBytes(), folder, existingDoiBong.getId() + "_logo");
            existingDoiBong.setLogo(logoUrl);
        }

        if (updateDoiBongDto.getAoChinhThuc() != null) {
            String aoChinhThucUrl = uploadToCloudinary(updateDoiBongDto.getAoChinhThuc().getBytes(), folder, existingDoiBong.getId() + "_ao_chinh_thuc");
            existingDoiBong.setAoChinhThuc(aoChinhThucUrl);
        }

        if (updateDoiBongDto.getAoDuBi() != null) {
            String aoDuBiUrl = uploadToCloudinary(updateDoiBongDto.getAoDuBi().getBytes(), folder, existingDoiBong.getId() + "_ao_du_bi");
            existingDoiBong.setAoDuBi(aoDuBiUrl);
        }

        return existingDoiBong;
    }

    @Override
    public void deleteDoiBong(Long id) {
        doiBongRepository.findById(id).ifPresentOrElse(
                doiBong -> {
                    try {
                        deleteCloudinaryFolder("doi-bong/" + doiBong.getId());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to delete Cloudinary folder", e);
                    }
                    doiBongRepository.delete(doiBong);
                },
                () -> {
                    throw new ResourceNotFoundException("Doi bong not found");
                }
        );
    }

    private void deleteCloudinaryFolder(String folderPath) throws Exception {
        Map deleteParams = ObjectUtils.asMap(
                "resource_type", "image",
                "type", "upload",
                "prefix", folderPath
        );
        cloudinary.api().deleteResourcesByPrefix(folderPath, deleteParams);
        cloudinary.api().deleteFolder(folderPath, ObjectUtils.emptyMap());
    }
}
