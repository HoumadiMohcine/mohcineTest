package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Client;

public interface ClientRespository extends JpaRepository<Client, Long>{
	
}
