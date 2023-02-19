package me.sitech.integration.domain.response;


public class ProfileUserResponse {
   private String id;
    private long createdTimestamp;
    private String userName;
    private boolean enabled;
    private String firstName;
    private String lastName;
    private String email;

 public String getId() {
  return id;
 }

 public void setId(String id) {
  this.id = id;
 }

 public long getCreatedTimestamp() {
  return createdTimestamp;
 }

 public void setCreatedTimestamp(long createdTimestamp) {
  this.createdTimestamp = createdTimestamp;
 }

 public String getUserName() {
  return userName;
 }

 public void setUserName(String userName) {
  this.userName = userName;
 }

 public boolean isEnabled() {
  return enabled;
 }

 public void setEnabled(boolean enabled) {
  this.enabled = enabled;
 }

 public String getFirstName() {
  return firstName;
 }

 public void setFirstName(String firstName) {
  this.firstName = firstName;
 }

 public String getLastName() {
  return lastName;
 }

 public void setLastName(String lastName) {
  this.lastName = lastName;
 }

 public String getEmail() {
  return email;
 }

 public void setEmail(String email) {
  this.email = email;
 }
}