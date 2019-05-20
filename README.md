# KASTEL-Requirements-Editor to PCM 
This prototypical Application uses the JSON-Export of the [KASTELEditor](https://github.com/Baakel/KastelEditor) and generates a Repository-Repository Model and a Composite-Component of the system according to the Palladio Component Model (PCM). Furthermore, a JSON-File is generated relating the generated Content with the Elements of the KASTELEditor-Model to provide means to trace back the PCM-Elements to their counter-parts of the KASTELEditor. 
A Software Architect can use the generated PCM-Model as a point to start and refine the model further. 

## Mapping between the KASTELEditor and the PCM-Model
The generation utilizes the following implicit Mapping between the first-class Model-Elements(first KASTELEditor Element, Second PCM-Element):

* Asset: Data-Type
* Service: Complete-Component (Here called "functionality component") AND Composite-Component (When used in one or more Hardgoals)
* Black Box Mechanism(BBM): Complete-Component
* Functional-Requirement: Interface

Out of these Elements, the "relationships" and goals are used to assemble the described system.
The Model is generated as follows: 

* BBM Components provide an interface with the same name as the BBM
* When a "Functional Requirement and Service Relationship" exists, the Components of the Service provide the Functional Requirement Interface
* A Service that is used in a "Hard Goal" is a composite-component containing the assembly-context of the a complete-component with the same name completed with "Functionality". The functionality component and the composite-component both provide the same interfaces through the "Functional Requirement and Service Relationship". A delegation connector exits from the provided roles of the composite component to the provided roles of the functional component.A assembly-context for a BBM complete-component exists for every BBM related with the service through an "Hard Goal". The functional-component requires the black box mechanisms interface and an assembly connector is created between the interfaces in the composite component. 

## Used libraries: 
* Gson 
