package org.example.service.impl;

import org.apache.poi.ss.usermodel.Row;
import org.example.config.DatabaseConfig;
import org.example.dto.request.CreateStockMovementRequest;
import org.example.dto.request.StockImportRequest;
import org.example.dto.response.Batch.BatchResponse;
import org.example.dto.response.Product.ProductResponse;
import org.example.entity.Warehouse;
import org.example.entity.enums.BatchStatus;
import org.example.entity.enums.StockMovementType;
import org.example.repository.*;
import org.example.router.AuthContextManager;
import org.example.service.StockMovementService;
import org.example.utils.importer.core.AbstractImportService;
import org.example.utils.importer.core.ImportError;
import org.example.utils.importer.core.ImportMapper;
import org.example.utils.importer.core.ImportValidator;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service để import dữ liệu tồn kho từ file Excel
 */
public class StockImportService extends AbstractImportService<StockImportRequest> {

    private final StockMovementService stockMovementService;
    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    public StockImportService(StockMovementService stockMovementService,
                              BatchRepository batchRepository,
                              ProductRepository productRepository,
                              WarehouseRepository warehouseRepository,
                              InventoryRepository inventoryRepository) {
        this.stockMovementService = stockMovementService;
        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Lấy mapper để chuyển đổi dòng Excel thành DTO
     */
    @Override
    protected ImportMapper<StockImportRequest> getMapper() {
        return new ImportMapper<StockImportRequest>() {
            @Override
            public StockImportRequest mapRow(Row row, int rowIndex) throws Exception {

                StockImportRequest request = new StockImportRequest();
                String typeStr = getCellValue(row, 0).trim().toUpperCase();
                try {
                    request.setMovementType(StockMovementType.valueOf(typeStr));
                } catch (IllegalArgumentException e) {
                    request.setMovementType(null);
                }
                request.setWarehouseId(Long.valueOf(getCellValue(row, 1).trim()));
                request.setProductId(Long.valueOf(getCellValue(row, 2).trim()));
                request.setBatchId(Long.valueOf(getCellValue(row, 3).trim()));
                request.setQuantity(Integer.valueOf(getCellValue(row, 4).trim()));
                request.setNote(getCellValue(row, 5).trim());
                return request;
            }

            @Override
            public String[] getRequiredHeaders() {
                return new String[]{
                        "Loại",
                        "Kho hàng",
                        "Mã sản phẩm",
                        "Mã lô",
                        "Số lượng",
                        "Ghi chú"
                };
            }
        };
    }

    /**
     * Lấy validator để validate dữ liệu
     */
    @Override
    protected ImportValidator<StockImportRequest> getValidator() {
        return (data, rowIndex) -> {
            List<ImportError> errors = new ArrayList<>();

            if (data.getMovementType() == null) {
                errors.add(new ImportError(rowIndex, "Loại", "Loại không được để trống"));
            }

            if (data.getWarehouseId() == null) {
                errors.add(new ImportError(rowIndex, "Kho hàng", "Kho hàng không được để trống"));
            } else {
                Optional<Warehouse> warehouse = warehouseRepository.findById(data.getWarehouseId());
                if (warehouse.isEmpty()) {
                    errors.add(new ImportError(rowIndex, "Kho hàng", "Kho hàng không tồn tại"));
                } else {
                    // Kiểm tra kho có hoạt động không
                    if (!warehouse.get().isActive()) {
                        errors.add(new ImportError(rowIndex, "Kho hàng", "Kho hàng không hoạt động"));
                    } else {
                        data.setWarehouseId(warehouse.get().getId());
                    }
                }
            }

            if (data.getProductId() == null) {
                errors.add(new ImportError(rowIndex, "Mã sản phẩm", "Mã sản phẩm không được để trống"));
            } else {
                Optional<ProductResponse> product = productRepository.findById(data.getProductId());
                if (product.isEmpty()) {
                    errors.add(new ImportError(rowIndex, "Mã sản phẩm", "Sản phẩm không tồn tại"));
                } else {
                    data.setProductId(product.get().getId());
                }

            }

            if (data.getBatchId() == null) {
                errors.add(new ImportError(rowIndex, "Mã lô", "Mã lô không được để trống"));
            } else {
                BatchResponse batch = batchRepository.findById(data.getBatchId());
                if (batch == null) {
                    errors.add(new ImportError(rowIndex, "Mã lô", "Lô hàng không tồn tại cho sản phẩm này"));
                } else {
                    if (!batch.getStatus().equals(BatchStatus.ACTIVE)) {
                        errors.add(new ImportError(rowIndex, "Mã lô", "Lô hàng không hoạt động"));
                    } else if (!batch.getProductId().equals(data.getProductId())) {
                        errors.add(new ImportError(rowIndex, "Mã lô", "Lô hàng không thuộc sản phẩm này"));
                    } else {
                        data.setBatchId(batch.getId());
                    }
                }
            }

            if (data.getQuantity() == null || data.getQuantity() <= 0) {
                errors.add(new ImportError(rowIndex, "Số lượng", "Số lượng phải > 0"));
            } else if (data.getQuantity() < 0) {
                errors.add(new ImportError(rowIndex, "Số lượng", "Số lượng không được âm"));
            }

            // ✅ CHỈ KIỂM TRA KHI XUẤT HOẶC CHUYỂN KHO (không check IMPORT/ADJUST)
            if (data.getMovementType() != null && data.getProductId() != null &&
                    data.getBatchId() != null && data.getWarehouseId() != null &&
                    data.getQuantity() != null) {

                if (data.getMovementType() == StockMovementType.EXPORT ||
                        data.getMovementType() == StockMovementType.TRANSFER) {

                    // Lấy tồn kho hiện tại
                    Integer currentQty = inventoryRepository.getCurrentQuantity(
                            data.getProductId(),
                            data.getBatchId(),
                            data.getWarehouseId()
                    );

                    if (currentQty == null) currentQty = 0;

                    if (currentQty < data.getQuantity()) {
                        errors.add(new ImportError(rowIndex, "Số lượng",
                                String.format("Không đủ hàng để xuất. Tồn kho: %d, Yêu cầu: %d",
                                        currentQty, data.getQuantity())));
                    }
                }
            }
            return errors;
        };
    }

    /**
     * Lưu dữ liệu hợp lệ vào database
     *
     * @param validData
     */
    @Override
    protected void saveData(List<StockImportRequest> validData) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);
            try {

                for (StockImportRequest request : validData) {
                    CreateStockMovementRequest movementRequest = new CreateStockMovementRequest();
                    movementRequest.setMovementType(request.getMovementType());
                    movementRequest.setWarehouseId(request.getWarehouseId());
                    movementRequest.setProductId(request.getProductId());
                    movementRequest.setBatchId(request.getBatchId());
                    movementRequest.setQuantity(request.getQuantity());
                    movementRequest.setNote(request.getNote());
                    
                    // ✅ Lấy user ID từ AuthContextManager
                    Long currentUserId = AuthContextManager.getUserId();
                    if (currentUserId == null) {
                        throw new RuntimeException("Vui lòng đăng nhập để thực hiện import");
                    }
                    movementRequest.setPerformedByUserId(currentUserId);

                    stockMovementService.createMovement(movementRequest);

                }
                connection.commit();
            } catch(Exception e) {
                connection.rollback();
                throw new RuntimeException("Lỗi khi import xuất nhập kho: " + e.getMessage(), e);
            } finally{
                connection.setAutoCommit(true);
            }
        }
    }
}
