package source_files.services.Image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import source_files.core.exception.DataNotFoundException;
import source_files.core.exception.FileException;
import source_files.core.utilities.ImageUtils;
import source_files.data.models.imageEntities.BrandImageEntity;
import source_files.repositories.image.BrandImageRepository;
import source_files.services.BusinessRules.ImageRules;
import source_files.services.external.CloudinaryServiceImpl;

import java.io.IOException;
import java.util.List;

import static source_files.core.exception.exceptionTypes.FileExceptionType.PHOTO_DELETE_FAILED;
import static source_files.core.exception.exceptionTypes.FileExceptionType.PHOTO_UPLOAD_FAILED;
import static source_files.core.exception.exceptionTypes.NotFoundExceptionType.IMAGE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BrandImageServiceImpl implements BrandImageService {
    private final BrandImageRepository repository;
    private final ImageRules rules;
    private final CloudinaryServiceImpl cloudinaryServiceImpl;

    @Override
    public BrandImageEntity create(MultipartFile file, String brandName) throws IOException {
        try {
            String imageUrl = cloudinaryServiceImpl.uploadFileBrand(file, brandName);
            return repository.save(BrandImageEntity.brandImageBuilder()
                    .name(brandName)
                    .type(file.getContentType())
                    .imageUrl(imageUrl)
                    .imageData(ImageUtils.compressImage(file.getBytes()))
                    .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileException(PHOTO_UPLOAD_FAILED);
        }
    }

    @Override
    public byte[] downloadImage(String fileName) {
        BrandImageEntity dbImageData = repository.findByName(fileName).orElse(null);
        assert dbImageData != null;
        return ImageUtils.decompressImage(dbImageData.getImageData());
    }

    @Override
    public BrandImageEntity getById(int id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException(IMAGE_NOT_FOUND));
    }

    @Override
    public List<BrandImageEntity> getAll() {
        List<BrandImageEntity> brandImages = repository.findAll();
        rules.checkDataList(brandImages);
        return brandImages;
    }

    @Override
    public void delete(int id) {
        try {
            if (cloudinaryServiceImpl.deleteFile(this.getById(id).getName())) {
                repository.delete(this.getById(id));
            }
        } catch (Exception e) {
            throw new FileException(PHOTO_DELETE_FAILED);
        }
    }
}