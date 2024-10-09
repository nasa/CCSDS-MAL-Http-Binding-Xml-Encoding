# CCSDS Message Abstraction Layer (MAL) Prototype using Http Binding & XML Encoding
* Visit CCSDS at [Homepage](https://public.ccsds.org/default.aspx)
* Visit MAL at [Wiki](https://en.wikipedia.org/wiki/Message_Abstraction_Layer)
* Visit HTTP Binding & XML Encoding at [specification](https://public.ccsds.org/Pubs/524x3b1.pdf)
## About
In order to verify the clarity and validity of the specification documents, two independent prototypes are implemented. 
After both prototypes are completed, they are verified by interoperability tests using a [test bed](https://github.com/esa/CCSDS_MO_TESTBEDS).
Four different types of tests were conducted and passed. 
1. Prototype-A Server to Prototype-B Client
2. Prototype-B Server to Prototype-A Client
3. Prototype-A Server to Prototype-A Client
4. Prototype-B Server to Prototype-B Client
## License
Copyright © 2017-2018 United States Government as represented by the Administrator of the National Aeronautics and Space Administration. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
## Installations
### Prerequisites
* Java-1.8
* Maven
### Modules
1. **CCSDS_MAL_Encode_HTTP**: XML Encoder converting `MAL java objects` to `XML` or an `XML` document to `MAL java objects`
2. **CCSDS_MAL_HEADER_HTTP**: Header Encoder converting HTTP headers into MAL header and vice versa
3. **CCSDS_MAL_TRANSPORT_HTTP**: Http Server to send and receive CCSDS messages
4. **CCSDS_MAL_IP_TEST**: Integration tests to verify Http Server is working as intended. 
### Instructions
1. Clone the repo
2. In `root` directory, run `mvn package`
   * Compiling java files
   * Running unit tests
   * Running integration tests
   * Creating a fat jar by combining all the libraries
   

