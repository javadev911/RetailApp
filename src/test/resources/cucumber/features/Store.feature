Feature: Retail Application To Add|Update Shop and to find nearest shop based on coordinates
  As a user
  I want to use a Retail Service
  So that I can add or update Shop details
  And I can also find nearest shops based on my geo location

  Scenario: Add or Update Shop details
    Given shop names as
    | shopName  |
    | M&S       |
    And shop addresses as
    | number       | postCode |
    | 107 - 115    | WC2E 9NT |
    When I call shops API
    Then saved or updated details should be returned in reponse
    
    Scenario: Find nearest shop based on my coordinates
    Given shop names as
    | shopName  |
    | TESCO     |
    | M&S       |
    And shop addresses as
    | number       | postCode |
    | 15           | E14 4QT  |
    | 107 - 115    | WC2E 9NT |
    And when these shop details are in application memory
    When I call findNearestShop API with my coordinates, latitude as "51.5052433", longitude as "-0.0211143"
    Then nearest shop "TESCO" should be returned