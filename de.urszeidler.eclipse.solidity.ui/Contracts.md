# Contracts

The basic profile for solidity dapps.


## Function 

An operation will be rendered as function with the functionCode value as code.

extends ===> :Property

name | type | doc
----|----
functionCode| -org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl@7bfbde18 (eProxyURI: pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#String)- | 
returnType|FunctionType -org.eclipse.uml2.uml.internal.impl.EnumerationImpl@57d9d5ff (name: FunctionType, visibility: <unset>) (isLeaf: false, isAbstract: false, isFinalSpecialization: false)- |the return type of the function
 
modifier| -org.eclipse.uml2.uml.internal.impl.ClassImpl@3e01e4dc (eProxyURI: pathmap://UML_METAMODELS/UML.metamodel.uml#Constraint)- |Applies the constrain an a modifier to the method.
 



## Delegate 

Let the generate produce delegate operations defined in the type of the property. 

extends ===> :Property



## Event 

An operation Tagges as Event will define an Event in the Contract.

extends ===> :Property



## Contract 

A class tagged with this stereotype will be generated as a contract.

extends ===> :Property

name | type | doc
----|----
ConstructorCode| -org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl@3b3146ee (eProxyURI: pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#String)- |Code for the contructor.
 
ConstructorArgs| -org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl@6948ed52 (eProxyURI: pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#String)- |The arguments of the constructor.
 



## Mapping 

Defines the mapping type. The value is defined by the 'value' property.

extends ===> :Property

name | type | doc
----|----
value| -org.eclipse.uml2.uml.internal.impl.ClassImpl@a82d235 (eProxyURI: pathmap://UML_METAMODELS/UML.metamodel.uml#Classifier)- |Define the value of the map.
 



## Library 

Defines class a an library.

extends ===> :Property



## Struct 

A class tagged with this stereotype will be generated as a struct. So normally it is models as an inner class.

extends ===> :Property



## GetterSetter 

Creates a getter and/or setter for an tagged property.

extends ===> :Property

name | type | doc
----|----
getter| -org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl@5ed7e698 (eProxyURI: pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#Boolean)- |Set to true will generate a getter method.
 
setter| -org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl@10508cb2 (eProxyURI: pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#Boolean)- |Set to true generates a setter method for the property.
 



## StandardizedContractAPI 

An interface tagged by this stereotype define an SCA.
A contract implementing such an interface will take only the defines methods and not explicitly inherited this interface.

extends ===> :Property



## Const 

Defines a return parameter as constant.

extends ===> :Property



## Import 

Defines an import for this model.

extends ===> :Property

name | type | doc
----|----
importFilter| -org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl@cbcbf2 (eProxyURI: pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#String)- |A filter for the imports.
 



