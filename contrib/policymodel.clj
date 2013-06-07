{"class" "eu.superhub.wp4.models.mobilitypolicy.PolicyModel",
 "indicatorTemplates"
 {"class"
  "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$IndicatorTemplates",
  "indicatorTemplate"
  [{"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$IndicatorTemplates$IndicatorTemplate",
    "name" "AveragePercentageOfCitizenUsingThisTransportType",
    "description"
    "Average Percentage of the population served by a transport type",
    "localization" nil,
    "id" "AveragePercentageOfCitizenUsingThisTransportType",
    "parameter"
    [{"dataType" "string",
      "restrictions"
      "transportType in ('PUBLIC_TRANSPORT','PRIVATE_TRANSPORT')",
      "minelements" 1,
      "name" "transportType",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "transportType",
      "maxelements" 1,
      "containertype" nil,
      "description"
      "Transport type of query from PUBLIC_TRANSPORT or PRIVATE_TRANSPORT"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "for $element in //SampleSeries[name='percentageOfCitizenUsingThisTransportType'] return (avg($element/sample/value), min($element/sample/begintime), max($element/sample/endtime))",
    "formulaDependency"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.FormulaDependency",
      "dependencyTemplateId"
      "percentageOfCitizenUsingThisTransportType",
      "selectionCondition" nil}]}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$IndicatorTemplates$IndicatorTemplate",
    "name" "producedCO2InGramproducedCOInGramAggregate",
    "description" "Aggregation of CO2 and CO level over time",
    "localization" nil,
    "id" "producedCO2InGramproducedCOInGramAggregate",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "for $element1 in //SampleSeries[name='producedCO2InGram']/sample for $element2 in //SampleSeries[name='producedCOInGram']/sample where $element1/begintime = $element2/begintime and $element1/endtime = $element2/endtime return (xs:double($element1/value + $element2/value), xs:double($element1/begintime), xs:double($element1/endtime))",
    "formulaDependency"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.FormulaDependency",
      "dependencyTemplateId" "producedCO2InGram",
      "selectionCondition" nil}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.FormulaDependency",
      "dependencyTemplateId" "producedCOInGram",
      "selectionCondition" nil}]}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$IndicatorTemplates$IndicatorTemplate",
    "name" "producedNOxInGramproducedSOxInGramAggregate",
    "description" "Aggregation of NOx and SOx level over time",
    "localization" nil,
    "id" "producedNOxInGramproducedSOxInGramAggregate",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "for $element1 in //SampleSeries[name='producedNOxInGram']/sample for $element2 in //SampleSeries[name='producedSOxInGram']/sample where $element1/begintime = $element2/begintime and $element1/endtime = $element2/endtime return (xs:double($element1/value + $element2/value), xs:double($element1/begintime), xs:double($element1/endtime))",
    "formulaDependency"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.FormulaDependency",
      "dependencyTemplateId" "producedNOxInGram",
      "selectionCondition" nil}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.FormulaDependency",
      "dependencyTemplateId" "producedSOxInGram",
      "selectionCondition" nil}]}]},
 "successIndicatorTemplates"
 {"class"
  "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$SuccessIndicatorTemplates",
  "successIndicatorTemplate"
  [{"formula"
    "for $element1 in //SampleSeries[name='producedCO2InGramproducedCOInGramAggregate'] for $element2 in //SampleSeries[name='producedNOxInGramproducedSOxInGramAggregate'] return (xs:double( ((max($element1/sample/value) - min($element1/sample/value)) + (max($element2/sample/value) - min($element2/sample/value))) div 2), min($element1/sample/begintime), max($element1/sample/endtime))",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "name" "stinkingGasRatio",
    "scorecalculationformula"
    "for $element1 in //SampleSeries[name='stinkingGasRatio']/sample return (xs:double(1 - $element1/value) , xs:double($element1/begintime) , xs:double($element1/endtime))",
    "formulaDependency"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.FormulaDependency",
      "dependencyTemplateId"
      "producedCO2InGramproducedCOInGramAggregate",
      "selectionCondition" nil}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.FormulaDependency",
      "dependencyTemplateId"
      "producedNOxInGramproducedSOxInGramAggregate",
      "selectionCondition" nil}],
    "class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$SuccessIndicatorTemplates$SuccessIndicatorTemplate",
    "localization" nil,
    "id" "stinkingGasRatio",
    "description" "Ratio of gas levels that do not smell very well"}]},
 "actionTemplates"
 {"class"
  "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$ActionTemplates",
  "actionTemplate"
  [{"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$ActionTemplates$ActionTemplate",
    "name" "ActionTemplateName",
    "description" "Action template description",
    "localization" nil,
    "id" "11",
    "parameter" nil,
    "formalexpression" "Action template formal expression",
    "expectedEffects" nil}]},
 "indicators"
 {"class" "eu.superhub.wp4.models.mobilitypolicy.Indicators",
  "indicator"
  [{"class" "eu.superhub.wp4.models.mobilitypolicy.Indicator",
    "samplingPeriod" 0,
    "name" "AveragePercentageOfCitizenUsingThisTransportTypeIndicator",
    "description"
    "Average Percentage of the population served by a transport type Indicator",
    "localizations" nil,
    "id" 5,
    "templateId" "AveragePercentageOfCitizenUsingThisTransportType",
    "parameterValue"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "transportType",
      "value" "PUBLIC_TRANSPORT"}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "startInterval",
      "value" "0"}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "endInterval",
      "value" "1000"}]}
   {"class" "eu.superhub.wp4.models.mobilitypolicy.Indicator",
    "samplingPeriod" 0,
    "name" "producedCO2InGramproducedCOInGramAggregateIndicator",
    "description"
    "Aggregation of CO2 and CO level over time Indicator",
    "localizations" nil,
    "id" 6,
    "templateId" "producedCO2InGramproducedCOInGramAggregate",
    "parameterValue"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "startInterval",
      "value" "0"}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "endInterval",
      "value" "1000"}]}
   {"class" "eu.superhub.wp4.models.mobilitypolicy.Indicator",
    "samplingPeriod" 0,
    "name" "producedNOxInGramproducedSOxInGramAggregateIndicator",
    "description"
    "Aggregation of NOx and SOx level over time Indicator",
    "localizations" nil,
    "id" 7,
    "templateId" "producedNOxInGramproducedSOxInGramAggregate",
    "parameterValue"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "startInterval",
      "value" "0"}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "endInterval",
      "value" "1000"}]}]},
 "policies"
 {"class" "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$Policies",
  "policy"
  [{"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$Policies$Policy",
    "name" "testPolicy",
    "description" "Test policy Description",
    "localizations" nil,
    "id" 23,
    "defaultimplementation" 0,
    "implementation"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.PolicyImplementation",
      "PolicyImplementationStatus" "final",
      "actions"
      {"class" "eu.superhub.wp4.models.mobilitypolicy.Actions",
       "action"
       [{"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action1",
         "description" "Description of action 1",
         "localizations" nil,
         "id" 12,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action2",
         "description" "Description of action 2",
         "localizations" nil,
         "id" 13,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action3",
         "description" "Description of action 3",
         "localizations" nil,
         "id" 14,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action4",
         "description" "Description of action 4",
         "localizations" nil,
         "id" 15,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action5",
         "description" "Description of action 5",
         "localizations" nil,
         "id" 16,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}]},
      "name" "testPolicyImplementation1",
      "description" "Test policy implementation description 1",
      "localizations" nil,
      "id" 17}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.PolicyImplementation",
      "PolicyImplementationStatus" "final",
      "actions"
      {"class" "eu.superhub.wp4.models.mobilitypolicy.Actions",
       "action"
       [{"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action1",
         "description" "Description of action 1",
         "localizations" nil,
         "id" 12,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action2",
         "description" "Description of action 2",
         "localizations" nil,
         "id" 13,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action3",
         "description" "Description of action 3",
         "localizations" nil,
         "id" 14,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action4",
         "description" "Description of action 4",
         "localizations" nil,
         "id" 15,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action5",
         "description" "Description of action 5",
         "localizations" nil,
         "id" 16,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}]},
      "name" "testPolicyImplementation2",
      "description" "Test policy implementation description 2",
      "localizations" nil,
      "id" 18}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.PolicyImplementation",
      "PolicyImplementationStatus" "final",
      "actions"
      {"class" "eu.superhub.wp4.models.mobilitypolicy.Actions",
       "action"
       [{"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action1",
         "description" "Description of action 1",
         "localizations" nil,
         "id" 12,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action2",
         "description" "Description of action 2",
         "localizations" nil,
         "id" 13,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action3",
         "description" "Description of action 3",
         "localizations" nil,
         "id" 14,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action4",
         "description" "Description of action 4",
         "localizations" nil,
         "id" 15,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action5",
         "description" "Description of action 5",
         "localizations" nil,
         "id" 16,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}]},
      "name" "testPolicyImplementation3",
      "description" "Test policy implementation description 3",
      "localizations" nil,
      "id" 19}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.PolicyImplementation",
      "PolicyImplementationStatus" "final",
      "actions"
      {"class" "eu.superhub.wp4.models.mobilitypolicy.Actions",
       "action"
       [{"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action1",
         "description" "Description of action 1",
         "localizations" nil,
         "id" 12,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action2",
         "description" "Description of action 2",
         "localizations" nil,
         "id" 13,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action3",
         "description" "Description of action 3",
         "localizations" nil,
         "id" 14,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action4",
         "description" "Description of action 4",
         "localizations" nil,
         "id" 15,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action5",
         "description" "Description of action 5",
         "localizations" nil,
         "id" 16,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}]},
      "name" "testPolicyImplementation4",
      "description" "Test policy implementation description 4",
      "localizations" nil,
      "id" 20}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.PolicyImplementation",
      "PolicyImplementationStatus" "draft",
      "actions"
      {"class" "eu.superhub.wp4.models.mobilitypolicy.Actions",
       "action"
       [{"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action1",
         "description" "Description of action 1",
         "localizations" nil,
         "id" 12,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action2",
         "description" "Description of action 2",
         "localizations" nil,
         "id" 13,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action3",
         "description" "Description of action 3",
         "localizations" nil,
         "id" 14,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action4",
         "description" "Description of action 4",
         "localizations" nil,
         "id" 15,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action5",
         "description" "Description of action 5",
         "localizations" nil,
         "id" 16,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}]},
      "name" "testPolicyImplementation5",
      "description" "Test policy implementation description 5",
      "localizations" nil,
      "id" 21}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.PolicyImplementation",
      "PolicyImplementationStatus" "final",
      "actions"
      {"class" "eu.superhub.wp4.models.mobilitypolicy.Actions",
       "action"
       [{"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action1",
         "description" "Description of action 1",
         "localizations" nil,
         "id" 12,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action2",
         "description" "Description of action 2",
         "localizations" nil,
         "id" 13,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action3",
         "description" "Description of action 3",
         "localizations" nil,
         "id" 14,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action4",
         "description" "Description of action 4",
         "localizations" nil,
         "id" 15,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}
        {"class" "eu.superhub.wp4.models.mobilitypolicy.Action",
         "name" "action5",
         "description" "Description of action 5",
         "localizations" nil,
         "id" 16,
         "templateId" "11",
         "parameterValue"
         [{"class"
           "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
           "fieldid" "transportType",
           "value" "PUBLIC_TRANSPORT"}]}]},
      "name" "testPolicyImplementation6",
      "description" "Test policy implementation description 6",
      "localizations" nil,
      "id" 22}],
    "purpose"
    {"class" "eu.superhub.wp4.models.mobilitypolicy.PolicyPurpose",
     "instantiatedSuccessIndicators"
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.SuccessIndicators",
      "successIndicator"
      [{"class"
        "eu.superhub.wp4.models.mobilitypolicy.SuccessIndicator",
        "name" "stinkingGasRatio",
        "description"
        "Ratio of gas levels that do not smell very well",
        "localizations" nil,
        "id" 8,
        "templateId" "stinkingGasRatio",
        "parameterValue"
        [{"class"
          "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
          "fieldid" "startInterval",
          "value" "0"}
         {"class"
          "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
          "fieldid" "endInterval",
          "value" "1000"}],
        "samplingPeriod" 0}]},
     "goal"
     [{"class" "eu.superhub.wp4.models.mobilitypolicy.Goal",
       "operationalobjectiveid" "9",
       "successIndicator"
       [{"class"
         "eu.superhub.wp4.models.mobilitypolicy.GoalSuccessIndicator",
         "successindicatorid" 8,
         "weight" 0.3,
         "threshold" 0.0}
        {"class"
         "eu.superhub.wp4.models.mobilitypolicy.GoalSuccessIndicator",
         "successindicatorid" 8,
         "weight" 0.7,
         "threshold" 0.0}],
       "weight" 0.5,
       "threshold" 0.0}
      {"class" "eu.superhub.wp4.models.mobilitypolicy.Goal",
       "operationalobjectiveid" "10",
       "successIndicator"
       [{"class"
         "eu.superhub.wp4.models.mobilitypolicy.GoalSuccessIndicator",
         "successindicatorid" 8,
         "weight" 0.3,
         "threshold" 0.0}
        {"class"
         "eu.superhub.wp4.models.mobilitypolicy.GoalSuccessIndicator",
         "successindicatorid" 8,
         "weight" 0.7,
         "threshold" 0.0}],
       "weight" 0.7,
       "threshold" 0.0}]}}]},
 "metricTemplates"
 {"class"
  "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates",
  "metricTemplate"
  [{"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "percentageOfCitizenUsingThisTransportType",
    "description"
    "Percentage of the population served by a transport type",
    "localization" nil,
    "id" "percentageOfCitizenUsingThisTransportType",
    "parameter"
    [{"dataType" "string",
      "restrictions"
      "transportType in ('PUBLIC_TRANSPORT','PRIVATE_TRANSPORT')",
      "minelements" 1,
      "name" "transportType",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "transportType",
      "maxelements" 1,
      "containertype" nil,
      "description"
      "Transport type of query from PUBLIC_TRANSPORT or PRIVATE_TRANSPORT"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "let $var_transportType := <FormulaParameter>transportType</FormulaParameter> let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> for $element in //citizenReport where  $element/transportType/text() = $var_transportType and $element/measuredInTimeInterval/orderOfSimulationDay >= $var_startInterval and $element/measuredInTimeInterval/orderOfSimulationDay <= $var_endInterval return ( xs:double($element/percentageOfCitizenUsingThisTransportType),xs:double($element/measuredInTimeInterval/orderOfSimulationDay),xs:double($element/measuredInTimeInterval/orderOfSimulationDay))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "numOfJourneyLegs",
    "description"
    "Number of the population served by a transport type and mode",
    "localization" nil,
    "id" "numOfJourneyLegs",
    "parameter"
    [{"dataType" "string",
      "restrictions"
      "transportType in ('PUBLIC_TRANSPORT','PRIVATE_TRANSPORT')",
      "minelements" 1,
      "name" "transportType",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "transportType",
      "maxelements" 1,
      "containertype" nil,
      "description"
      "Transport type of query from PUBLIC_TRANSPORT or PRIVATE_TRANSPORT"}
     {"dataType" "string",
      "restrictions"
      "transportType in ('WALK','BYCICLE','MOTORBIKE','CAR','TRAM','BUS','METRO')",
      "minelements" 1,
      "name" "transportMode",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "transportMode",
      "maxelements" 1,
      "containertype" nil,
      "description"
      "transport mode of query from WALK or BYCICLE or MOTORBIKE or CAR or TRAM or BUS or METRO"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "let $var_transportType := <FormulaParameter>transportType</FormulaParameter> let $var_transportMode := <FormulaParameter>transportMode</FormulaParameter> let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> for $element in //transportModeReport where  $element/transportType/text() = $var_transportType and $element/transportMode/text() = $var_transportMode and $element/measuredInTimeInterval/orderOfSimulationDay >= $var_startInterval and $element/measuredInTimeInterval/orderOfSimulationDay <= $var_endInterval return ( xs:double($element/numOfJourneyLegs),xs:double($element/measuredInTimeInterval/orderOfSimulationDay),xs:double($element/measuredInTimeInterval/orderOfSimulationDay))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "numOfJourneysBasedOnMainModeCriterion",
    "description"
    "Number of the population served by a transport type and mode based on main mode criterion",
    "localization" nil,
    "id" "numOfJourneysBasedOnMainModeCriterion",
    "parameter"
    [{"dataType" "string",
      "restrictions"
      "transportType in ('PUBLIC_TRANSPORT','PRIVATE_TRANSPORT')",
      "minelements" 1,
      "name" "transportType",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "transportType",
      "maxelements" 1,
      "containertype" nil,
      "description"
      "Transport type of query from PUBLIC_TRANSPORT or PRIVATE_TRANSPORT"}
     {"dataType" "string",
      "restrictions"
      "transportType in ('WALK','BYCICLE','MOTORBIKE','CAR','TRAM','BUS','METRO')",
      "minelements" 1,
      "name" "transportMode",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "transportMode",
      "maxelements" 1,
      "containertype" nil,
      "description"
      "transport mode of query from WALK or BYCICLE or MOTORBIKE or CAR or TRAM or BUS or METRO"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "let $var_transportType := <FormulaParameter>transportType</FormulaParameter> let $var_transportMode := <FormulaParameter>transportMode</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> for $element in //transportModeReport where  $element/transportType/text() = $var_transportType and $element/transportMode/text() = $var_transportMode and $element/measuredInTimeInterval/orderOfSimulationDay >= $var_startInterval and $element/measuredInTimeInterval/orderOfSimulationDay <= $var_endInterval return ( xs:double($element/numOfJourneysBasedOnMainModeCriterion),xs:double($element/measuredInTimeInterval/orderOfSimulationDay),xs:double($element/measuredInTimeInterval/orderOfSimulationDay))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "producedCO2InGram",
    "description" "Air quality: CO2 level",
    "localization" nil,
    "id" "producedCO2InGram",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> for $element in //emissions where $element/measuredInTimeInterval/orderOfSimulationDay >= $var_startInterval and $element/measuredInTimeInterval/orderOfSimulationDay <= $var_endInterval return ( xs:double($element/producedCO2InGram),xs:double($element/measuredInTimeInterval/orderOfSimulationDay),xs:double($element/measuredInTimeInterval/orderOfSimulationDay))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "producedCOInGram",
    "description" "Air quality: CO level",
    "localization" nil,
    "id" "producedCOInGram",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> for $element in //emissions where $element/measuredInTimeInterval/orderOfSimulationDay >= $var_startInterval and $element/measuredInTimeInterval/orderOfSimulationDay <= $var_endInterval return ( xs:double($element/producedCOInGram),xs:double($element/measuredInTimeInterval/orderOfSimulationDay),xs:double($element/measuredInTimeInterval/orderOfSimulationDay))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "producedNOxInGram",
    "description" "Air quality: NOx level",
    "localization" nil,
    "id" "producedNOxInGram",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> for $element in //emissions where $element/measuredInTimeInterval/orderOfSimulationDay >= $var_startInterval and $element/measuredInTimeInterval/orderOfSimulationDay <= $var_endInterval return ( xs:double($element/producedNOxInGram),xs:double($element/measuredInTimeInterval/orderOfSimulationDay),xs:double($element/measuredInTimeInterval/orderOfSimulationDay))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "producedSOxInGram",
    "description" "Air quality: SOx level",
    "localization" nil,
    "id" "producedSOxInGram",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> for $element in //emissions where $element/measuredInTimeInterval/orderOfSimulationDay >= $var_startInterval and $element/measuredInTimeInterval/orderOfSimulationDay <= $var_endInterval return ( xs:double($element/producedSOxInGram),xs:double($element/measuredInTimeInterval/orderOfSimulationDay),xs:double($element/measuredInTimeInterval/orderOfSimulationDay))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "producedPM10InGram",
    "description" "Air quality: PM10 standard level",
    "localization" nil,
    "id" "producedPM10InGram",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> for $element in //emissions where $element/measuredInTimeInterval/orderOfSimulationDay >= $var_startInterval and $element/measuredInTimeInterval/orderOfSimulationDay <= $var_endInterval return ( xs:double($element/producedPM10InGram),xs:double($element/measuredInTimeInterval/orderOfSimulationDay),xs:double($element/measuredInTimeInterval/orderOfSimulationDay))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "travelTimeInMilliseconds",
    "description" "Transport Mode average travelling time",
    "localization" nil,
    "id" "travelTimeInMilliseconds",
    "parameter"
    [{"dataType" "string",
      "restrictions"
      "transportType in ('PUBLIC_TRANSPORT','PRIVATE_TRANSPORT')",
      "minelements" 1,
      "name" "transportType",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "transportType",
      "maxelements" 1,
      "containertype" nil,
      "description"
      "Transport type of query from PUBLIC_TRANSPORT or PRIVATE_TRANSPORT"}
     {"dataType" "string",
      "restrictions"
      "transportType in ('WALK','BYCICLE','MOTORBIKE','CAR','TRAM','BUS','METRO')",
      "minelements" 1,
      "name" "transportMode",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "transportMode",
      "maxelements" 1,
      "containertype" nil,
      "description"
      "transport mode of query from WALK or BYCICLE or MOTORBIKE or CAR or TRAM or BUS or METRO"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}],
    "formula"
    "let $var_transportType := <FormulaParameter>transportType</FormulaParameter> let $var_transportMode := <FormulaParameter>transportMode</FormulaParameter> let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> for $element in //averageTravelTime where  $element/transportType/text() = $var_transportType and $element/transportMode/text() = $var_transportMode and $element/measuredInTimeInterval/orderOfSimulationDay >= $var_startInterval and $element/measuredInTimeInterval/orderOfSimulationDay <= $var_endInterval return ( xs:double($element/travelTimeInMilliseconds),xs:double($element/measuredInTimeInterval/orderOfSimulationDay),xs:double($element/measuredInTimeInterval/orderOfSimulationDay))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "producedCO2InGramExceedsLevelNumberDays",
    "description" "Number of days where CO2 limit Level is exceeded",
    "localization" nil,
    "id" "producedCO2InGramExceedsLevelNumberDays",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "maxLevel",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "maxLevel",
      "maxelements" 1,
      "containertype" nil,
      "description" "Maximum level to be taken into account"}],
    "formula"
    "let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> let $var_maxLevel := <FormulaParameter>maxLevel</FormulaParameter> for $element in //emissionsArray return ( xs:double(count(//emissionsArray/emissions[producedCO2InGram > $var_maxLevel])), xs:double($var_startInterval), xs:double($var_endInterval))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "producedCOInGramExceedsLevelNumberDays",
    "description" "Number of days where CO limit Level is exceeded",
    "localization" nil,
    "id" "producedCOInGramExceedsLevelNumberDays",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "maxLevel",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "maxLevel",
      "maxelements" 1,
      "containertype" nil,
      "description" "Maximum level to be taken into account"}],
    "formula"
    "let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> let $var_maxLevel := <FormulaParameter>maxLevel</FormulaParameter> for $element in //emissionsArray return ( xs:double(count(//emissionsArray/emissions[producedCOInGram > $var_maxLevel])), xs:double($var_startInterval), xs:double($var_endInterval))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "producedNOxInGramExceedsLevelNumberDays",
    "description" "Number of days where NOx limit Level is exceeded",
    "localization" nil,
    "id" "producedNOxInGramExceedsLevelNumberDays",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "maxLevel",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "maxLevel",
      "maxelements" 1,
      "containertype" nil,
      "description" "Maximum level to be taken into account"}],
    "formula"
    "let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> let $var_maxLevel := <FormulaParameter>maxLevel</FormulaParameter> for $element in //emissionsArray return ( xs:double(count(//emissionsArray/emissions[producedNOxInGram > $var_maxLevel])), xs:double($var_startInterval), xs:double($var_endInterval))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "producedSOxInGramExceedsLevelNumberDays",
    "description" "Number of days where SOx limit Level is exceeded",
    "localization" nil,
    "id" "producedSOxInGramExceedsLevelNumberDays",
    "parameter"
    [{"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "startInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "startInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "Start interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "endInterval",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "endInterval",
      "maxelements" 1,
      "containertype" nil,
      "description" "End interval for query"}
     {"dataType" "int",
      "restrictions" "",
      "minelements" 1,
      "name" "maxLevel",
      "class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameter",
      "mandatory" true,
      "localization" nil,
      "id" "maxLevel",
      "maxelements" 1,
      "containertype" nil,
      "description" "Maximum level to be taken into account"}],
    "formula"
    "let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> let $var_maxLevel := <FormulaParameter>maxLevel</FormulaParameter> for $element in //emissionsArray return ( xs:double(count(//emissionsArray/emissions[producedSOxInGram > $var_maxLevel])), xs:double($var_startInterval), xs:double($var_endInterval))",
    "dataSource" "simulationreport.xml"}
   {"class"
    "eu.superhub.wp4.models.mobilitypolicy.PolicyModel$MetricTemplates$MetricTemplate",
    "name" "producedPM10InGramExceedsLevelNumberDays",
    "description" "Number of days where PM10 limit Level is exceeded",
    "localization" nil,
    "id" "producedPM10InGramExceedsLevelNumberDays",
    "parameter" nil,
    "formula"
    "let $var_startInterval := <FormulaParameter>startInterval</FormulaParameter> let $var_endInterval := <FormulaParameter>endInterval</FormulaParameter> let $var_maxLevel := <FormulaParameter>maxLevel</FormulaParameter> for $element in //emissionsArray return ( xs:double(count(//emissionsArray/emissions[producedPM10InGram > $var_maxLevel])), xs:double($var_startInterval), xs:double($var_endInterval))",
    "dataSource" "simulationreport.xml"}]},
 "metrics"
 {"class" "eu.superhub.wp4.models.mobilitypolicy.Metrics",
  "metric"
  [{"class" "eu.superhub.wp4.models.mobilitypolicy.Metric",
    "name" "percentageOfCitizenUsingThisTransportTypeMetric",
    "description"
    "Percentage of the population served by a transport type metric",
    "localizations" nil,
    "id" 1,
    "templateId" "percentageOfCitizenUsingThisTransportType",
    "parameterValue"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "transportType",
      "value" "PUBLIC_TRANSPORT"}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "startInterval",
      "value" "0"}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "endInterval",
      "value" "1000"}]}
   {"class" "eu.superhub.wp4.models.mobilitypolicy.Metric",
    "name" "producedCO2InGramMetric",
    "description" "Air quality: CO2 level Metric",
    "localizations" nil,
    "id" 2,
    "templateId" "producedCO2InGram",
    "parameterValue"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "startInterval",
      "value" "0"}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "endInterval",
      "value" "1000"}]}
   {"class" "eu.superhub.wp4.models.mobilitypolicy.Metric",
    "name" "producedCOInGramMetric",
    "description" "Air quality: CO level Metric",
    "localizations" nil,
    "id" 3,
    "templateId" "producedCOInGram",
    "parameterValue"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "startInterval",
      "value" "0"}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "endInterval",
      "value" "1000"}]}
   {"class" "eu.superhub.wp4.models.mobilitypolicy.Metric",
    "name" "producedSOxInGramMetric",
    "description" "Air quality: SOx level Metric",
    "localizations" nil,
    "id" 4,
    "templateId" "producedSOxInGram",
    "parameterValue"
    [{"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "startInterval",
      "value" "0"}
     {"class"
      "eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue",
      "fieldid" "endInterval",
      "value" "1000"}]}]}}
