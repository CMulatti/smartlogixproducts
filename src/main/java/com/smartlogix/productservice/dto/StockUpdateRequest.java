package com.smartlogix.productservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

//we use this DTO to receive the stock decrease from ORDERSERVICE.
// ORDERSERVICE will post a JSON , "porductId": 1, "quantity": 2
/**No response DTO needed because we just want to say "Ok. stock updated", but if we wanted to show the previous stock + the new one + the message,
then yes, maybe we could create a response dto to do that. DTOs responses are needed when the response body contains structured data.
 Creating DTOs is not about how many fields we have, it is about "what does this data represent?" Here, that'd be "a stock update operation request"*/


@Getter
@Setter
@NoArgsConstructor
public class StockUpdateRequest {
    private Long productId;
    private Integer quantity; //how many units to subtract from stock
}

