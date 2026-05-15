package com.stallmart.management;

import com.stallmart.management.dto.OperationLogDTO;
import com.stallmart.management.dto.OperationLogRecordParams;
import java.util.List;

public interface OperationLogService {

    void record(OperationLogRecordParams params);

    List<OperationLogDTO> listPlatformLogs();

    List<OperationLogDTO> listVendorLogs(long storeId);
}
