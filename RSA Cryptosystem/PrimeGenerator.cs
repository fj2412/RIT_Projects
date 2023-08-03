// Author: Feng Jiang
// Prime Number Generation Helper Class

using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Numerics;
using System.Security.Cryptography;
using System.Threading;
using System.Threading.Tasks;
using Extensions;

namespace Messenger
{
    /// <summary>
    /// The class that contains methods to generate and validate the numbers generated
    /// </summary>
       /// <summary>
    /// The class that contains methods to generate and validate the numbers generated
    /// </summary>
    class PrimeGenerator
    {
        private int bits;
        private int counts;
        private int counter;
        private Object sync = new Object();
        
        /// <summary>
        /// The constructor for prime number generation object
        /// </summary>
        /// <param name="bits">the number of bits divided by 8</param>
        /// <param name="counts">the number of prime numbers that should be generated</param>
        public PrimeGenerator(int bits, int counts)
        {
            this.bits = bits;
            this.counts = counts;
            counter = 0;
        }
        
        /// <summary>
        /// Generates a random positive Big Integer
        /// </summary>
        /// <param name="numBytes">the number of bytes</param>
        /// <returns>Unsigned Big Integer</returns>
        private BigInteger GenerateRandomNumber(int numBytes)
        {
            RNGCryptoServiceProvider rng = new RNGCryptoServiceProvider();
            var bytes = new byte[numBytes];
            rng.GetBytes(bytes);
            rng.Dispose();
            return new BigInteger(bytes, true);
        }
        
        /// <summary>
        /// Method that stimulates an infinite while loop for parallel loop usage
        /// </summary>
        /// <returns>boolean: true</returns>
        private IEnumerable<bool> Infinite()  
        {  
            while (true)  
            {  
                yield return true;  
            }  
        }  
        
        /// <summary>
        /// Main algorithm for program. It will generate random numbers and check whether each number
        /// generated is prime, it will stop when it met the count requirement from user.
        /// </summary>
        public ConcurrentBag<BigInteger> PrimeCheck()
        {
            CancellationTokenSource tokenSource = new CancellationTokenSource();
            ConcurrentBag<BigInteger> bag = new ConcurrentBag<BigInteger>();
            Task.Factory.StartNew(() =>
            {
                lock (sync)
                {
                    if (counter >= counts)
                    {
                        tokenSource.Cancel();
                    }
                }
            });
            var options = new ParallelOptions() 
            {
                CancellationToken = tokenSource.Token
            };
            try
            {
                bag = new ConcurrentBag<BigInteger>();
                Parallel.ForEach(Infinite(), options, (ignored, loopstate) =>
                {
                    var bigInt = GenerateRandomNumber(bits);
                    if (bigInt % 2 == 0)
                    {
                        // definitely not prime
                    }
                    else if (bigInt.IsProbablyPrime())
                    {
                        lock (sync)
                        {
                            if (counter < counts)
                            {
                                bag.Add(bigInt);
                                counter++;
                            }
                        }
                    }
                    options.CancellationToken.ThrowIfCancellationRequested();
                    if (counter == counts)
                    {
                        loopstate.Stop();
                    }
                });
            }
            catch (OperationCanceledException)
            {
                // Cancelling Exception
            }

            return bag;
        }
    }
}

namespace Extensions{
    /// <summary>
    /// The extension class for Big Integer type
    /// </summary>
    public static class BigIntExtension{
        
        /// <summary>
        /// The extension method for big integer class which checks whether the current big integer
        /// is prime
        /// </summary>
        /// <param name="value">The big integer to be checked</param>
        /// <param name="k">Number of rounds of testing to perform</param>
        /// <returns>boolean: true if prime, false otherwise</returns>
        public static bool IsProbablyPrime(this BigInteger value, int k = 10)
        {
            if (value <= 1) return false;
            if (k <= 0) k = 10;
            BigInteger d = value - 1;
            int s = 0;
            while (d % 2 == 0)
            {
                d /= 2;
                s += 1;
            }

            Byte[] bytes = new Byte[value.ToByteArray().LongLength];
            BigInteger a;
            for (int i = 0; i < k; i++)
            {
                do
                {
                    var Gen = new RNGCryptoServiceProvider();
                    Gen.GetBytes(bytes);
                    a = new BigInteger(bytes);
                } while (a < 2 || a >= value - 2);

                BigInteger x = BigInteger.ModPow(a, d, value);
                if (x == 1 || x == value - 1) continue;
                for (int r = 1; r < s; r++)
                {
                    x = BigInteger.ModPow(x, 2, value);
                    if (x == 1) return false;
                    if (x == value - 1) break;
                }

                if (x != value - 1) return false;
            }
            return true;
        }
    }
}