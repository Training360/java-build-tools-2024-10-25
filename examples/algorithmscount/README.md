# Számlálás tétele

## Elmélet

Az algoritmus bemenete egy n elemű lista. A feladat az, hogy számoljuk meg azokat az elemeket,
amelyekre igaz egy feltétel. Például számoljuk meg a 15-nél nagyobb számokat egy listában.

### Elméleti megvalósítás

*	Változó deklarálása számlálónak
*	Ciklusban iterálás
*	Feltétel teljesülése esetén számláló növelése
*	Számláló visszaadása

> A videóban 02:09-nél sajnos hibásan hangzik el az, hogy a String karakterek listája. 
> Valójában a String mögött egy karaktertömb áll.

### Gyakorlati megvalósítás

```java
public int countLetters(String s, char c) {
    int count = 0;
    for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) == c) {
            count++;
        }
    }
    return count;
}
```

```java
public int countElderly(List<Trainer> trainers, int minAge ) {
      int count = 0;
      for (Trainer trainer: trainers) {
          if (trainer.getAge() >= minAge) {
              count++;
          }
      }
      return count;
  }
```

## Ellenőrző kérdések

* Mi a bemenete és a kimenete a számlálás algoritmusának?
* Mi legyen a kezdőértéke a majdani visszatérési értéket tároló változónak?

## Gyakorlati feladatok

Az `algorithmscount` csomagba dolgozz!

### Gyakorlati feladat - Magassági korlát

Egy játszótéri eszközön csak egy bizonyos magasságnál magasabb gyerekek játszhatnak. A `height.Height`
osztályban legyen egy `countChildrenWithHeightGreaterThan()` metódus, amelynek két paramétere egy
egész számokat tartalmazó lista, amelyben egy csapatnyi gyerek centiméterben mért
magassága található, valamint egy meghatározott magassági korlát (szintén centiméterben)! A metódus feladata,
hogy adja vissza, az adott gyerekcsoportból hányan játszhatnak a játszótéri játékon.

### Gyakorlati feladat - Nagy összegű bankszámlák

Hozz létre egy `bankaccount.BankAccount` osztályt a szükséges attribútumokkal:

* `nameOfOwner`, a számla tulajdonosának neve
* `accountNumber`, a számlaszám
* `balance`, egyenleg

Feladat egy metódus megírása a `BankAccountCondition` osztályban, ami megszámlálja, hány olyan számla van,
amelynek az aktuális egyenlege meghaladja a paraméterként kapott alsó határt. A metódus nevét megtudhatod a tesztesetből.

<!-- [rating feedback=java-algorithmscount-bankszamlak] -->

### Gyakorlati feladat - Kis összegű tranzakciók

Hozz létre egy `transaction.Transaction` osztályt, a szükséges attribútumokkal:

* `accountNumber`, számlaszám
* `transactionType` (CREDIT vagy DEBIT, egy külön `TransactionType` enum)
* `amount`, a tranzakció összege

Feladat egy metódus megírása a `TransactionService` osztályban, ami megszámlálja, hány olyan tranzakció van,
amely credit és a paraméterként kapott összeghatárnál kisebb értékű. A metódus nevét megtudhatod a tesztesetből.

<!-- [rating feedback=java-algorithmscount-tranzakciok] -->

### Gyakorlati feladat - Kétjegyű számok számjegyei

A `Digits` osztályba írj egy `getCountOfNumbers()` metódust, amely a következő matematikai feladat megoldását adja vissza:
Hány olyan kétjegyű pozitív egész szám van, amelyben az egyik számjegy 5-tel nagyobb a másiknál?