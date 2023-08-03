// Author: Feng Jiang
// Messenger Program

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace Messenger
{
    /// <summary>
    /// The Program class for RSA messaging system
    /// </summary>
    class Program
    {
        /// <summary>
        /// main method for RSA messaging, it will determine functionality based on command
        /// line input.
        /// </summary>
        /// <param name="args">the program argument for performing operation</param>
        public static async Task Main(string[] args)
        {
            try
            {
                var client = new HttpClient();
                var path = Directory.GetCurrentDirectory();
                var publicPath = Path.Combine(path, "public.key");
                var privatePath = Path.Combine(path, "private.key");
                var htmlPath = "http://kayrun.cs.rit.edu:5000/";
                HttpResponseMessage response;
                
                if (args.Length is > 3 or < 2)
                {
                    Console.WriteLine("Error: Invalid number of arguments");
                    Console.WriteLine("Usage: dotnet run <option> <other arguments");
                }
                else if (args.Length == 2 && args[0] != "sendMsg")
                {
                    if (args[0] == "keyGen")
                    {
                        var keySize = 0;
                        if (!Int32.TryParse(args[1], out keySize))
                        {
                            Console.WriteLine("Argument 2 is not a number");
                            Console.WriteLine("Usage: dotnet run <option> <other arguments");
                        }
                        else if (keySize < 1024)
                        {
                            Console.WriteLine("Key size has to be greater than 1024");
                            Console.WriteLine("Usage: dotnet run <option> <other arguments");
                        }
                        else if (keySize % 2 != 0)
                        {
                            Console.WriteLine("Key size has to be even");
                            Console.WriteLine("Usage: dotnet run <option> <other arguments");
                        }
                        else
                        {
                            // connect to server and get the json object
                            htmlPath += "Key/jsb@cs.rit.edu";
                            response =
                                await client.GetAsync(htmlPath);
                            response.EnsureSuccessStatusCode();
                            var responseBody = await response.Content.ReadAsStringAsync();
                            var jsonObject = JsonConvert.DeserializeObject<PublicKeyData>(responseBody);

                            var rng = new Random();
                            var offset = rng.NextDouble() * (1.2 - 0.8) + 0.8;
                            var pSize = (int)((keySize / 2) * offset);
                            var generatorP = new PrimeGenerator(pSize / 8, 1);
                            var generatorQ = new PrimeGenerator((keySize - pSize) / 8, 1);
                            var bag1 = generatorP.PrimeCheck();
                            var bag2 = generatorQ.PrimeCheck();
                            BigInteger p;
                            BigInteger q;
                            bag1.TryTake(out p);
                            bag2.TryTake(out q);

                            var N = p * q;
                            var r = (p - 1) * (q - 1);
                            // getting the e value from server 
                            var keyBytes = Convert.FromBase64String(jsonObject.Key);
                            var eBytes = new Byte[4];
                            Array.Copy(keyBytes, 0, eBytes, 0, 4);
                            Array.Reverse(eBytes);
                            var eLength = BitConverter.ToInt32(eBytes);
                            var EBytes = new Byte[eLength];
                            Array.Copy(keyBytes, 4, EBytes, 0, eLength);
                            var E = new BigInteger(EBytes);

                            // use it as my own e value
                            var NBytes = N.ToByteArray();
                            var nBytes = BitConverter.GetBytes(NBytes.Length);
                            Array.Reverse(eBytes);
                            Array.Reverse(nBytes);
                            
                            var eArray = CombineByteArrays(eBytes, EBytes);
                            var nArray = CombineByteArrays(nBytes, NBytes);
                            var publicBytes = CombineByteArrays(eArray, nArray);

                            var publicKeyData = new PublicKeyData()
                            {
                                Key = Convert.ToBase64String(publicBytes)
                            };
                            var publicKey = JsonConvert.SerializeObject(publicKeyData);
                            await File.WriteAllTextAsync(publicPath, publicKey);

                            var D = ModInverse(E, r);
                            var DBytes = D.ToByteArray();
                            var dBytes = BitConverter.GetBytes(DBytes.Length);
                            Array.Reverse(dBytes);
                            var dArray = CombineByteArrays(dBytes, DBytes);
                            var privateBytes = CombineByteArrays(dArray, nArray);
                            
                            List<string> emailList = new List<string>();
                            var privateKeyData = new PrivateKeyData()
                            {
                                Email = emailList,
                                Key = Convert.ToBase64String(privateBytes)
                            };
                            var privateKey = JsonConvert.SerializeObject(privateKeyData);
                            await File.WriteAllTextAsync(privatePath, privateKey);

                            client.Dispose();
                        }
                    }
                    else if (args[0] == "sendKey")
                    {
                        if (File.Exists(publicPath) &&
                            File.Exists(privatePath))
                        {
                            htmlPath += "Key/" + args[1];
                            var fileContent = await File.ReadAllTextAsync(publicPath);
                            var publicKeyObject = JsonConvert.DeserializeObject<PublicKeyData>(fileContent);
                            publicKeyObject.Email = args[1];
                            var publicKey = JsonConvert.SerializeObject(publicKeyObject);
                            var content = new StringContent(publicKey, Encoding.UTF8, "application/json");
                            response = await client.PutAsync(htmlPath, content);
                            response.EnsureSuccessStatusCode();

                            fileContent = await File.ReadAllTextAsync(privatePath);
                            var privateKeyObject = JsonConvert.DeserializeObject<PrivateKeyData>(fileContent);
                            if (!privateKeyObject.Email.Contains(args[1]))
                            {
                                privateKeyObject.Email.Add(args[1]);
                            }

                            var privateKey = JsonConvert.SerializeObject(privateKeyObject);
                            await File.WriteAllTextAsync(privatePath, privateKey);
                            Console.WriteLine("Key saved");
                            client.Dispose();
                        }
                        else
                        {
                            Console.WriteLine("Public and/or Private key files are missing");
                        }
                    }
                    else if (args[0] == "getKey")
                    {
                        htmlPath += "Key/" + args[1];
                        response = await client.GetAsync(htmlPath);
                        response.EnsureSuccessStatusCode();
                        var responseBody = await response.Content.ReadAsStringAsync();
                        var jsonObject = JsonConvert.DeserializeObject<PublicKeyData>(responseBody);
                        var converted = JsonConvert.SerializeObject(jsonObject);
                        var keyFilePath = Path.Combine(path, args[1] + ".key");
                        await File.WriteAllTextAsync(keyFilePath, converted);
                        client.Dispose();
                    }
                    else if (args[0] == "getMsg")
                    {
                        htmlPath += "Message/" + args[1];
                        var fileContent = await File.ReadAllTextAsync(privatePath);
                        var privateKeyObject = JsonConvert.DeserializeObject<PrivateKeyData>(fileContent);
                        if (privateKeyObject.Email.Contains(args[1]))
                        {
                            response = await client.GetAsync(htmlPath);
                            var responseBody = await response.Content.ReadAsStringAsync();
                            var dataFromServer = JsonConvert.DeserializeObject<MessageData>(responseBody);
                            var serverMessage = dataFromServer.Content;
                            var keyBytes = Convert.FromBase64String(privateKeyObject.Key);
                            var actualMessage = Decode(serverMessage, keyBytes);
                            
                            Console.WriteLine(actualMessage);
                            response.EnsureSuccessStatusCode();
                            client.Dispose();
                        }
                        else
                        {
                            Console.WriteLine("Key does not exist for " + args[1]);
                        }
                    }
                    else
                    {
                        Console.WriteLine("Valid options are keyGen, sendKey, getKey, sendMsg, and getMsg");
                        Console.WriteLine("Note program options are case sensitive");
                        Console.WriteLine("Usage: dotnet run <option> <other arguments>");
                    }
                } 
                else if (args.Length == 3)
                {
                    if (args[0] == "sendMsg")
                    {
                        htmlPath += "Message/" + args[1];
                        var keyPath = path + "/" + args[1] + ".key";

                        if (File.Exists(keyPath))
                        {
                            var fileContent = await File.ReadAllTextAsync(keyPath);
                            var publicKeyObject = JsonConvert.DeserializeObject<PublicKeyData>(fileContent);
                            var keyBytes = Convert.FromBase64String(publicKeyObject.Key);
                            var m = args[2];
                            var encodedMessage = Encode(m, keyBytes);
                            var messageObject = new MessageData()
                            {
                                Email = args[1],
                                Content = encodedMessage
                            };
                            var message = JsonConvert.SerializeObject(messageObject);
                            var content = new StringContent(message, Encoding.UTF8, "application/json");
                            response = await client.PutAsync(htmlPath, content);
                            response.EnsureSuccessStatusCode();
                            Console.WriteLine("Message written");
                            client.Dispose();
                        }
                        else
                        {
                            Console.WriteLine("Key does not exist for " + args[1]);
                        }
                    }
                    else
                    {
                        Console.WriteLine("Valid options are keyGen, sendKey, getKey, sendMsg, and getMsg");
                        Console.WriteLine("Note program options are case sensitive");
                        Console.WriteLine("Usage: dotnet run <option> <other arguments>");
                    }
                }
                else
                {
                    Console.WriteLine("Error: Invalid number of arguments");
                    Console.WriteLine("Usage: dotnet run <option> <other arguments>");
                }
            }
            catch (Exception)
            {
                Console.WriteLine("Server request error");
                // General Exception
            }
        }
        
        /// <summary>
        /// Combine 2 byte arrays and return it
        /// </summary>
        /// <param name="fir">first byte array to combine</param>
        /// <param name="sec">second byte array to combine</param>
        /// <returns>a combined byte array</returns>
        public static byte[] CombineByteArrays(byte[] fir, byte[] sec) {
            return fir.Concat(sec).ToArray();
        }

        /// <summary>
        /// Function to perform mod inverse
        /// </summary>
        /// <param name="a">BigInteger to inverse</param>
        /// <param name="n">BigInteger</param>
        /// <returns>An modulo inversed big integer</returns>
        public static BigInteger ModInverse(BigInteger a, BigInteger n)
        {
            BigInteger i = n, v = 0, d = 1;
            while (a > 0)
            {
                BigInteger t = i / a, x = a;
                a = i % x;
                i = x;
                x = d;
                d = v - t * x;
                v = x;
            }
            v %= n;
            if (v<0) v = (v+n)%n;
            return v;
        }
        
        /// <summary>
        /// Function to perform encoding of string
        /// </summary>
        /// <param name="plaintext">the string to encode</param>
        /// <param name="keyBytes">the key byte array for encryption</param>
        /// <returns>an encoded base64 string</returns>
        public static string Encode(string plaintext, Byte[] keyBytes) {
            var contentBytes = Encoding.UTF8.GetBytes(plaintext);
            var encodedInt = new BigInteger(contentBytes);
            
            var eBytes = keyBytes.Take(4).ToArray();
            Array.Reverse(eBytes);
            var eLength = BitConverter.ToInt32(eBytes);
            
            var EBytes = keyBytes.Skip(4).Take(eLength).ToArray();
            var e = new BigInteger(EBytes);
            
            var nBytes = keyBytes.Skip(4 + eLength).Take(4).ToArray();
            Array.Reverse(nBytes);
            var nLength = BitConverter.ToInt32(nBytes);
            
            var NBytes = keyBytes.Skip(4 + eLength + 4).Take(nLength).ToArray();
            var n = new BigInteger(NBytes);

            var encryptedMessageInt = BigInteger.ModPow(encodedInt, e, n);
            var encryptedMessageBytes = encryptedMessageInt.ToByteArray();
            return Convert.ToBase64String(encryptedMessageBytes);
        }
        
        /// <summary>
        /// Function to decoding of string
        /// </summary>
        /// <param name="encodedData">the encoded string to decode</param>
        /// <param name="keyBytes">the key byte array for decryption</param>
        /// <returns>an UTF8 decoded string</returns>
        public static string Decode(string encodedData, Byte[] keyBytes) {
            var contentBytes = Convert.FromBase64String(encodedData);
            var encodedInt = new BigInteger(contentBytes);
            

            var dBytes = keyBytes.Take(4).ToArray();
            Array.Reverse(dBytes);
            var dLength = BitConverter.ToInt32(dBytes);

            var DBytes = keyBytes.Skip(4).Take(dLength).ToArray();
            var d = new BigInteger(DBytes);

            var nBytes = keyBytes.Skip(4 + dLength).Take(4).ToArray();
            Array.Reverse(nBytes);
            var nLength = BitConverter.ToInt32(nBytes);

            var NBytes = keyBytes.Skip(4 + dLength + 4).Take(nLength).ToArray();
            var n = new BigInteger(NBytes);

            var decryptedMessageInt = BigInteger.ModPow(encodedInt, d, n);
            var decryptedMessageBytes = decryptedMessageInt.ToByteArray();
            return Encoding.UTF8.GetString(decryptedMessageBytes);
        }

    }

    /// <summary>
    /// class structure for public key json
    /// </summary>
    class PublicKeyData
    {
        /// <summary>
        /// Email string
        /// </summary>
        public string Email { get; set; }
        
        /// <summary>
        /// Key string
        /// </summary>
        public string Key { get; set; }
    }

    /// <summary>
    /// class structure for private key json
    /// </summary>
    class PrivateKeyData
    {
        /// <summary>
        /// List of Emails as string
        /// </summary>
        public IList<string> Email { get; set; }
        
        /// <summary>
        /// Key string
        /// </summary>
        public string Key { get; set; }
    }

    /// <summary>
    /// class structure for message data
    /// </summary>
    class MessageData
    {
        /// <summary>
        /// Email string
        /// </summary>
        public string Email { get; set; }
        
        /// <summary>
        /// string content
        /// </summary>
        public string Content { get; set; }
        
    }
}