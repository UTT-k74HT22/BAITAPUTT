package org.example.service.impl;

import org.apache.poi.ss.usermodel.Row;
import org.example.dto.request.ImportProductRequest;
import org.example.dto.request.Product.CreateProductRequest;
import org.example.entity.Category;
import org.example.entity.Manufacturer;
import org.example.entity.enums.ProductStatus;
import org.example.repository.CategoryRepository;
import org.example.repository.ManufacturerReponsitory;
import org.example.repository.ProductRepository;
import org.example.utils.importer.core.AbstractImportService;
import org.example.utils.importer.core.ImportError;
import org.example.utils.importer.core.ImportMapper;
import org.example.utils.importer.core.ImportValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service để import Product từ file Excel
 *
 * Cách sử dụng:
 * 1. User chọn file Excel
 * 2. Gọi previewFromFile() để xem trước và validate
 * 3. Nếu OK, gọi applyImport() để lưu vào DB
 */
public class ProductImportService extends AbstractImportService<ImportProductRequest> {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ManufacturerReponsitory manufacturerRepository;

    public ProductImportService(ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               ManufacturerReponsitory manufacturerRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.manufacturerRepository = manufacturerRepository;
    }

    /**
     * Định nghĩa cách map từ Excel row sang DTO
     */
    @Override
    protected ImportMapper<ImportProductRequest> getMapper() {
        return new ImportMapper<ImportProductRequest>() {
            @Override
            public ImportProductRequest mapRow(Row row, int rowIndex) {
                ImportProductRequest dto = new ImportProductRequest();

                // Map từng column
                dto.setCode(getCellValue(row, 0));
                dto.setName(getCellValue(row, 1));
                dto.setCategoryCode(getCellValue(row, 2));
                dto.setManufacturerCode(getCellValue(row, 3));
                dto.setBarcode(getCellValue(row, 4));
                dto.setDosageForm(getCellValue(row, 5));
                dto.setUnit(getCellValue(row, 6));
                dto.setDescription(getCellValue(row, 7));

                // Parse price
                String priceStr = getCellValue(row, 8);
                if (!priceStr.isEmpty()) {
                    try {
                        dto.setStandardPrice(new BigDecimal(priceStr));
                    } catch (NumberFormatException e) {
                        dto.setStandardPrice(BigDecimal.ZERO);
                    }
                }

                // Parse boolean
                String requiresPrescriptionStr = getCellValue(row, 9);
                dto.setRequiresPrescription(
                        requiresPrescriptionStr.equalsIgnoreCase("Yes") ||
                        requiresPrescriptionStr.equalsIgnoreCase("True") ||
                        requiresPrescriptionStr.equals("1")
                );

                return dto;
            }

            @Override
            public String[] getRequiredHeaders() {
                return new String[]{
                        "Code", "Name", "Category Code", "Manufacturer Code",
                        "Barcode", "Dosage Form", "Unit", "Description",
                        "Standard Price", "Requires Prescription"
                };
            }
        };
    }

    /**
     * Định nghĩa các rule validate
     */
    @Override
    protected ImportValidator<ImportProductRequest> getValidator() {
        return (data, rowIndex) -> {
            List<ImportError> errors = new ArrayList<>();

            // Validate Code
            if (data.getCode() == null || data.getCode().trim().isEmpty()) {
                errors.add(new ImportError(rowIndex, "Code", "Code is required"));
            } else if (productRepository.existsByCode(data.getCode())) {
                errors.add(new ImportError(rowIndex, "Code", "Code already exists: " + data.getCode()));
            }

            // Validate Name
            if (data.getName() == null || data.getName().trim().isEmpty()) {
                errors.add(new ImportError(rowIndex, "Name", "Name is required"));
            }

            // Validate Category Code
            if (data.getCategoryCode() == null || data.getCategoryCode().trim().isEmpty()) {
                errors.add(new ImportError(rowIndex, "Category Code", "Category Code is required"));
            } else {
                Category category = categoryRepository.findByCode(data.getCategoryCode());
                if (category == null) {
                    errors.add(new ImportError(rowIndex, "Category Code",
                        "Category not found: " + data.getCategoryCode()));
                }
            }

            // Validate Manufacturer Code (optional nhưng nếu có thì phải tồn tại)
            if (data.getManufacturerCode() != null && !data.getManufacturerCode().trim().isEmpty()) {
                if (!manufacturerRepository.ExistsByCode(data.getManufacturerCode())) {
                    errors.add(new ImportError(rowIndex, "Manufacturer Code",
                        "Manufacturer not found: " + data.getManufacturerCode()));
                }
            }

            // Validate Price
            if (data.getStandardPrice() != null && data.getStandardPrice().compareTo(BigDecimal.ZERO) < 0) {
                errors.add(new ImportError(rowIndex, "Standard Price", "Price must be positive"));
            }

            return errors;
        };
    }

    /**
     * Lưu dữ liệu hợp lệ vào database
     */
    @Override
    protected void saveData(List<ImportProductRequest> validData) throws Exception {
        for (ImportProductRequest dto : validData) {
            CreateProductRequest createRequest = new CreateProductRequest();
            createRequest.setCode(dto.getCode());
            createRequest.setName(dto.getName());
            createRequest.setBarcode(dto.getBarcode());
            createRequest.setDosageForm(dto.getDosageForm());
            createRequest.setUnit(dto.getUnit());
            createRequest.setDescription(dto.getDescription());
            createRequest.setStandardPrice(dto.getStandardPrice());
            createRequest.setRequiresPrescription(
                dto.getRequiresPrescription() != null ? dto.getRequiresPrescription() : false
            );
            createRequest.setStatus(ProductStatus.ACTIVE);

            // Get Category ID by code
            Category category = categoryRepository.findByCode(dto.getCategoryCode());
            if (category != null) {
                createRequest.setCategoryId(category.getId());
            }

            // Get Manufacturer ID by code (optional)
            if (dto.getManufacturerCode() != null && !dto.getManufacturerCode().isEmpty()) {
                List<Manufacturer> manufacturers = manufacturerRepository.findAll();
                for (Manufacturer m : manufacturers) {
                    if (m.getCode().equals(dto.getManufacturerCode())) {
                        createRequest.setManufacturerId(m.getId());
                        break;
                    }
                }
            }

            productRepository.insert(createRequest);
        }
    }
}

