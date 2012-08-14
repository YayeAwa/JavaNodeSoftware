package org.vamdc.tapservice.util;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.xsams.schema.XSAMSData;

public class XSAMSMetrics {
	
	private XSAMSData doc;
	
	public XSAMSMetrics(XSAMSData metrics){
		doc=metrics;
	}
	
	public int getMetric(HeaderMetrics metric){
		switch (metric){
		case VAMDC_COUNT_ATOMS:
			return this.getCountAtoms();
		case VAMDC_COUNT_MOLECULES:
			return this.getCountMolecules();
		case VAMDC_COUNT_SPECIES:
			return this.getCountSpecies();
		case VAMDC_COUNT_COLLISIONS:
			return this.getCountCollisions();
		case VAMDC_COUNT_RADIATIVE:
			return this.getCountRadiative();
		case VAMDC_COUNT_NONRADIATIVE:
			return this.getCountNonRadiative();
		case VAMDC_COUNT_SOURCES:
			return this.getCountSources();
		case VAMDC_COUNT_STATES:
			return this.getCountStates();
		case VAMDC_APPROX_SIZE:
			return 1;
		}
		return 0;
	}
	
	/**
	 * Check if the document is completely empty.
	 * @return true if count_species and count_processes are equal to zero
	 */
	public boolean isEmpty(){
		return (this.getCountSpecies()== 0 && this.getCountProcesses()==0);
	}

	
	private int getCountStates(){
		return 0;
	}
	
	private int getCountProcesses(){
		return getCountCollisions()+getCountRadiative()+getCountNonRadiative();
	}
	
	private int getCountCollisions() {//Collisions
		if (doc.getProcesses()!=null 
				&& doc.getProcesses().getCollisions()!=null 
				&& doc.getProcesses().getCollisions().getCollisionalTransitions()!=null)	
			return doc.getProcesses().getCollisions().getCollisionalTransitions().size();
		return 0; 
	}
	
	private int getCountRadiative(){//radiative transitions
		if (doc.getProcesses()!=null 
				&& doc.getProcesses().getRadiative()!=null 
				&& doc.getProcesses().getRadiative().getRadiativeTransitions()!=null)
			return doc.getProcesses().getRadiative().getRadiativeTransitions().size();
		return 0;
	}
	
	private int getCountNonRadiative(){//non-radiative transitions
		if (doc.getProcesses()!=null 
				&& doc.getProcesses().getNonRadiative()!=null 
				&& doc.getProcesses().getNonRadiative().getNonRadiativeTransitions()!=null)
			return doc.getProcesses().getNonRadiative().getNonRadiativeTransitions().size();
		return 0;
	}
	
	private int getCountSpecies(){
		return getCountAtoms()+getCountMolecules()+getCountParticles()+getCountSolids();
	}
	
	private int getCountAtoms(){
		if (doc.getSpecies()!=null 
				&& doc.getSpecies().getAtoms()!=null 
				&& doc.getSpecies().getAtoms().getAtoms()!=null)
			return doc.getSpecies().getAtoms().getAtoms().size();
		return 0;
	}
	
	private int getCountMolecules(){
		if (doc.getSpecies()!=null 
				&& doc.getSpecies().getMolecules()!=null 
				&& doc.getSpecies().getMolecules().getMolecules()!=null)
			return doc.getSpecies().getMolecules().getMolecules().size();
		return 0;
	}
	
	private int getCountParticles(){
		if (doc.getSpecies()!=null 
				&& doc.getSpecies().getParticles()!=null
				&& doc.getSpecies().getParticles().getParticles()!=null)
			return doc.getSpecies().getParticles().getParticles().size();
		return 0;
	}
	
	private int getCountSolids(){
		if (doc.getSpecies()!=null 
				&& doc.getSpecies().getSolids()!=null 
				&& doc.getSpecies().getSolids().getSolids()!=null)
			return doc.getSpecies().getSolids().getSolids().size();
		return 0;
	}
	
	private int getCountSources(){
		if (doc.getSources()!=null)
			return doc.getSources().getSources().size();
		return 0;
	}
}
