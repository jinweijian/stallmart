package com.stallmart.management;

import com.stallmart.management.dto.VendorWorkspaceDTO;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.user.dto.UserProfileDTO;
import java.util.List;

public interface VendorWorkspaceService {

    VendorWorkspaceDTO buildVendorWorkspace(StoreDTO store);

    List<UserProfileDTO> listUsersForStore(long storeId);
}
