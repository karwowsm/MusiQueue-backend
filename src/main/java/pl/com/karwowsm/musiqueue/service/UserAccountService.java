package pl.com.karwowsm.musiqueue.service;

import pl.com.karwowsm.musiqueue.api.request.UserAccountCreateRequest;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;

public interface UserAccountService {

    UserAccount create(UserAccountCreateRequest request);
}
