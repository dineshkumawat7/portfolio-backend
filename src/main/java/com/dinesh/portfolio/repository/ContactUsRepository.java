package com.dinesh.portfolio.repository;

import com.dinesh.portfolio.entity.ContactUs;

import java.util.List;

public interface ContactUsRepository {
    ContactUs save(ContactUs contactUs);
    List<ContactUs> findAll();
    ContactUs findById(Long id);
    ContactUs update(ContactUs contactUs);
    void deleteById(Long id);
}
