package com.vmlens.tutorialCopyOnWrite;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicAdress {

    private AtomicReference<AddressValue> addressValue;

    @Override
    public String toString() {
        AddressValue local = addressValue.get();
        return "street=" + local.getStreet() + ",city=" + local.getCity() + ",phoneNumber=" + local.getPhoneNumber();
    }
    public AtomicAdress(String street, String city, String phone) {
        this.addressValue = new AtomicReference<>(new AddressValue( street,  city,  phone));
    }
    public void update(String street ,String city ) {
        addressValue.getAndSet(new AddressValue(  street,  city,  addressValue.get().getPhoneNumber() ));
    }
}
