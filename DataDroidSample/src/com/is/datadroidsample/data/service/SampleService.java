package com.is.datadroidsample.data.service;

import android.os.Bundle;

import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;
import com.is.datadroidsample.data.exception.MyCustomRequestException;
import com.is.datadroidsample.data.requestmanager.SampleRequestFactory;

public class SampleService extends RequestService {

	@Override
    protected int getMaximumNumberOfThreads() {
        return 3;
    }

    @Override
    public Operation getOperationForType(int requestType) {
        switch (requestType) {
            /*case PoCRequestFactory.REQUEST_TYPE_PERSON_LIST:
                return new PersonListOperation();
            case PoCRequestFactory.REQUEST_TYPE_CITY_LIST:
                return new CityListOperation();
            case PoCRequestFactory.REQUEST_TYPE_CITY_LIST_2:
                return new CityList2Operation();
            case PoCRequestFactory.REQUEST_TYPE_AUTHENTICATION:
                return new AuthenticationOperation();
            case PoCRequestFactory.REQUEST_TYPE_CUSTOM_REQUEST_EXCEPTION:
                return new CustomRequestExceptionOperation();
            case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_LIST:
                return new CrudSyncPhoneListOperation();
            case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_DELETE:
                return new CrudSyncPhoneDeleteOperation();
            case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_ADD:
            case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_EDIT:
                return new CrudSyncPhoneAddEditOperation();
            case PoCRequestFactory.REQUEST_TYPE_RSS_FEED:
                return new RssFeedOperation();*/
        }
        return null;
    }

    @Override
    protected Bundle onCustomRequestException(Request request, CustomRequestException exception) {
        if (exception instanceof MyCustomRequestException) {
            Bundle bundle = new Bundle();
            bundle.putString(SampleRequestFactory.BUNDLE_EXTRA_ERROR_MESSAGE,
                    "MyCustomRequestException thrown.");
            return bundle;
        }
        return super.onCustomRequestException(request, exception);
    }

}
