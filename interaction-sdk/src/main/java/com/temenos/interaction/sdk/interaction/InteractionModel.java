package com.temenos.interaction.sdk.interaction;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;

import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;

/**
 * This class holds information about the interaction model
 */
public class InteractionModel {

	private List<IMResourceStateMachine> resourceStateMachines = new ArrayList<IMResourceStateMachine>();

	/**
	 * Construct an empty model
	 */
	public InteractionModel() {
	}	
	
	/**
	 * Construct an initial model from odata4j metadata
	 * @param edmDataServices odata4j metadata 
	 */
	public InteractionModel(EdmDataServices edmDataServices) {
		for (EdmEntitySet entitySet : edmDataServices.getEntitySets()) {
			addResourceStateMachine(createInitialResourceStateMachine(entitySet));
		}
	}
	
	/**
	 * Construct an initial model from entity metadata
	 * @param metadata metadata 
	 */
	public InteractionModel(Metadata metadata) {
		for (EntityMetadata entityMetadata : metadata.getEntitiesMetadata().values()) {
			addResourceStateMachine(createInitialResourceStateMachine(entityMetadata));
		}
	}

	/**
	 * Create an initial RSM with a collection and entity state
	 * @param entitySet Entity metadata
	 * @return resource state machine
	 */
	public IMResourceStateMachine createInitialResourceStateMachine(EdmEntitySet entitySet) {
		EdmEntityType entityType = entitySet.getType();
		String entityName = entityType.getName();
		String collectionStateName = entitySet.getName();
		String entityStateName = entityName.toLowerCase();
		String mappedEntityProperty = entityType.getKeys().size() > 0 ? entityType.getKeys().get(0) : "id";
		String pathParametersTemplate = getUriTemplateParameters(entityType);
		return new IMResourceStateMachine(entityName, collectionStateName, entityStateName, mappedEntityProperty, pathParametersTemplate);
	}

	/**
	 * Create an initial RSM with a collection and entity state
	 * @param entityMetadata Entity metadata
	 * @return resource state machine
	 */
	public IMResourceStateMachine createInitialResourceStateMachine(EntityMetadata entityMetadata) {
		return createInitialResourceStateMachine(entityMetadata, HttpMethod.GET);
	}
	
	/**
	 * Create an initial RSM with a collection and entity state
	 * @param entityMetadata Entity metadata
	 * @param methodGetEntity Method for GET entity 
	 * @return resource state machine
	 */
	public IMResourceStateMachine createInitialResourceStateMachine(EntityMetadata entityMetadata, String methodGetEntity) {
		String entityName = entityMetadata.getEntityName();
		String collectionStateName = entityName + "s";
		String entityStateName = entityName.toLowerCase();
		List<String> idFields = entityMetadata.getIdFields();
		String mappedEntityProperty = idFields.size() > 0 ? idFields.get(0) : "id";
		String pathParametersTemplate = getUriTemplateParameters(entityMetadata);
		return new IMResourceStateMachine(entityName, collectionStateName, entityStateName, methodGetEntity, mappedEntityProperty, pathParametersTemplate);
	}
	
	public String getUriTemplateParameters(EntityMetadata entityMetadata) {
		String paramTemplate = "";
		List<String> keys = entityMetadata.getIdFields();
		for(String key : keys) {
			if(!paramTemplate.equals("")) {
				paramTemplate += ",";				
			}
			String keyPropertyType = entityMetadata.getTermValue(key, TermValueType.TERM_NAME);
			if(keyPropertyType != null && isQuotedUriTemplateParameter(keyPropertyType)) {
				paramTemplate += "'{" + (keys.size() > 1 ? key : "id") + "}'";		//These types should be enclosed in single quotes				
			}
			else {
				paramTemplate += "{" + (keys.size() > 1 ? key : "id") + "}";				
			}
		}
		return paramTemplate;		
	}

	private boolean isQuotedUriTemplateParameter(String entityType) {
		return entityType.equals(TermValueType.IMAGE) ||
				entityType.equals(TermValueType.TIMESTAMP) ||
				entityType.equals(TermValueType.DATE) ||
				entityType.equals(TermValueType.TEXT) ||
				entityType.equals(TermValueType.ENCRYPTED_TEXT);
	}

	public String getUriTemplateParameters(EdmEntityType entityType) {
		String paramTemplate = "";
		List<String> keys = entityType.getKeys();
		for(String key : keys) {
			if(!paramTemplate.equals("")) {
				paramTemplate += ",";				
			}
			EdmType keyPropertyType = entityType.findDeclaredProperty(key).getType();
			if(keyPropertyType != null && isQuotedUriTemplateParameter(keyPropertyType)) {
				paramTemplate += "'{" + (keys.size() > 1 ? key : "id") + "}'";		//These types should be enclosed in single quotes				
			}
			else {
				paramTemplate += "{" + (keys.size() > 1 ? key : "id") + "}";				
			}
		}
		return paramTemplate;		
	}
	
	private boolean isQuotedUriTemplateParameter(EdmType type) {
		return type.equals(EdmSimpleType.DATETIME) || type.equals(EdmSimpleType.STRING);
	}
	
	public void addResourceStateMachine(IMResourceStateMachine resourceStateMachine) {
		resourceStateMachines.add(resourceStateMachine);
	}
	
	public List<IMResourceStateMachine> getResourceStateMachines() {
		return resourceStateMachines;
	}
	
	public IMResourceStateMachine findResourceStateMachine(String entityName) {
		for(IMResourceStateMachine rsm : resourceStateMachines) {
			if(rsm.getEntityName().equals(entityName)) {
				return rsm;
			}
		}
		return null;
	}
}