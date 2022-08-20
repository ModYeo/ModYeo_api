package com.co.kr.modyeo.api.advertisement.service;

import com.co.kr.modyeo.api.advertisement.domain.enumerate.AdvertisementType;
import com.co.kr.modyeo.api.advertisement.domain.request.AdvertisementCreateRequest;
import com.co.kr.modyeo.api.advertisement.domain.request.AdvertisementUpdateRequest;
import com.co.kr.modyeo.api.advertisement.domain.response.AdvertisementDetail;
import com.co.kr.modyeo.api.advertisement.domain.response.AdvertisementResponse;

import java.util.List;

public interface AdvertisementService {
    void createAdvertisement(AdvertisementCreateRequest advertisementCreateRequest);

    List<AdvertisementResponse> getAdvertisements(AdvertisementType advertisementType);

    AdvertisementDetail getAdvertisement(Long id);

    void updateAdvertisement(AdvertisementUpdateRequest advertisementUpdateRequest);

    void deleteAdvertisement(Long id);
}
