package com.example.demo.metier;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.CompteRepository;
import com.example.demo.dao.OperationRepository;
import com.example.demo.entities.Compte;
import com.example.demo.entities.CompteCourant;
import com.example.demo.entities.Operation;
import com.example.demo.entities.Retrait;
import com.example.demo.entities.Versement;
@Service
@Transactional
public class IBanqueMetierImp implements IbanqueMetier{
	@Autowired
	CompteRepository compteRepository ; 
	@Autowired
	OperationRepository operationRepository; 
	public Compte consulterCompte(String codeCpte) {
		Compte cp = compteRepository.findOne(codeCpte); 
		if(cp==null) throw new RuntimeException("compte introuvable") ; 
		return cp ; 
	}
	public void verser(String codeCpte , double montant) {
		Compte cp = consulterCompte(codeCpte); 
		Versement v = new Versement(new Date() , montant , cp) ; 
		operationRepository.save(v); 
		cp.setSolde(cp.getSolde() + montant);
	}
	public void retirer(String codeCpte , double montant) {
		Compte cp = consulterCompte(codeCpte); 
		double facilitesCaisse = 0 ; 
		if(cp instanceof CompteCourant )
			facilitesCaisse = ((CompteCourant) cp).getDecouvert() ; 
		if(cp.getSolde() + facilitesCaisse < montant)
			throw new RuntimeException("compte insuffisant") ; 
		Retrait r = new Retrait(new Date() , montant , cp) ; 
		operationRepository.save(r); 
		cp.setSolde(cp.getSolde() - montant);		
	}
	public void virement(String codeCpte1 , String codeCpte2 , double montant) {
		retirer(codeCpte1 , montant) ; 
		verser(codeCpte2 , montant) ; 
	}
	public Page<Operation> listOperation(String codeCpte , int page , int size){
		return operationRepository.listOperation(codeCpte, new PageRequest(page, size)) ; 
	}
}
