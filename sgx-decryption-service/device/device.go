package device

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/pem"
	"io/ioutil"
	"log"
)

// DEBUG - use key pairs from file
const DEBUG = true
const RSAOAEP = false

// Device global state data
type Device struct {
	signKey  *rsa.PrivateKey // Signing key-pair
	decKey   *rsa.PrivateKey // Decryption key-pair
	rootHash []byte          // Root hash in the Merkle Tree Log
}

// Init initializes the device
// Set initial RTH
// Generate RSA keys
func (d *Device) Init(initialHash []byte) *Device {
	d.rootHash = initialHash

	if DEBUG == true {
		d.decKey = debugImportRSAKey("test_set/crypt.pem")
		d.signKey = debugImportRSAKey("test_set/verif.pem")
	} else {
		d.decKey = generateKeyPair()
		d.signKey = generateKeyPair()
	}

	return d
}

// ----------- ECALLs (Interface functions) -------------

// Decrypt some ciphertext after verifying proofs that the request have been logged
func (d *Device) Decrypt(ciphertext []byte) (plaintext []byte, err error) {

	label := []byte("record")
	rng := rand.Reader

	// Verify π: R in H'

	// Verify ρ: H' extends H

	// result := dec(dk, R)

	if RSAOAEP == true {

		plaintext, err = rsa.DecryptOAEP(sha256.New(), rng, d.decKey, ciphertext, label)
		if err != nil {
			log.Printf("Error from OAEP decryption: %s\n", err)
		}

	} else {

		plaintext, err = rsa.DecryptPKCS1v15(rng, d.decKey, ciphertext)
		if err != nil {
			log.Printf("Error from PKCS1v15 decryption: %s\n", err)
		}
	}

	// H := H'
	return plaintext, err
}

// SignRootTreeHash returns  RTH and sign(sha256(RTH + nonce)) from device
func (d *Device) SignRootTreeHash(nonce []byte) (rth []byte, sig []byte) {

	rng := rand.Reader
	h := sha256.Sum256(append(d.rootHash, nonce...))

	signature, err := rsa.SignPKCS1v15(rng, d.signKey, crypto.SHA256, h[:])
	if err != nil {
		log.Fatal(err)
	}
	return d.rootHash, signature
}

// ExportPubKey returns the public keys generated by the device (handeled during attestation to provide authentication)
func (d *Device) ExportPubKey() (encryptionKey, verificationKey []byte) {
	encryptionKey = publicKeyToPEM(d.decKey.PublicKey)
	verificationKey = publicKeyToPEM(d.signKey.PublicKey)
	return
}

// ---------- Proof verification functions ------------

// def traverse(node, presence_list):

//     if node.has_key("Hash"):
//         h = node["Hash"]
//         presence_list.append(h)
//         return h

//     l = traverse(node.get("Left"), presence_list)
//     r = traverse(node.get("Right"), presence_list)

//     return hashlib.sha256(l + r).hexdigest()

// traverseProof traverses the proof tree
func traverseProof(node string, order [32]byte) {

	return
}

// verifyProofOfPresence parses the json formatted proof, and verifies the result, returns true or false
func (d *Device) verifyProofOfPresence(str string) bool {

	// presence list

	// Check if current RTH and proof RTH are equal

	// Check if proof_val actually is included in the proof

	// Verify by comparing RTH with the re-computed proof_tree

	return true
}

// verifyProofOfExtension parses the json formatted proof, and verifies the result, returns true or false
func (d *Device) verifyProofOfExtension(str string) bool {

	return true
}

// ---------- AUX functions ------------

// generateKeyPair will generate a pair of RSA keys
func generateKeyPair() *rsa.PrivateKey {
	reader := rand.Reader
	bitSize := 2048
	key, err := rsa.GenerateKey(reader, bitSize)
	if err != nil {
		log.Fatal(err)
	}

	return key
}

func publicKeyToPEM(pk rsa.PublicKey) (pubBytes []byte) {
	PubASN1, err := x509.MarshalPKIXPublicKey(&pk)
	if err != nil {
		log.Fatal(err)
	}

	pubBytes = pem.EncodeToMemory(&pem.Block{
		Type:  "RSA PUBLIC KEY",
		Bytes: PubASN1,
	})
	return
}

func privateKeyToPEM(sk rsa.PrivateKey) (privBytes []byte) {
	PrivASN1 := x509.MarshalPKCS1PrivateKey(&sk)

	privBytes = pem.EncodeToMemory(&pem.Block{
		Type:  "RSA PRIVATE KEY",
		Bytes: PrivASN1,
	})
	return
}

func debugImportRSAKey(filename string) *rsa.PrivateKey {

	pemkey, err := ioutil.ReadFile(filename) // "test_set/crypto.pem"
	if err != nil {
		log.Fatal(err)
	}

	pemBlock, _ := pem.Decode(pemkey)
	if pemBlock == nil {
		log.Fatal("No PEM block decoded")
	}

	key, err := x509.ParsePKCS1PrivateKey(pemBlock.Bytes)
	// key, err := x509.ParsePKCS8PrivateKey(pemBlock.Bytes)
	if err != nil {
		log.Fatal(err)
	}

	return key
}
