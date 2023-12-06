package app

import (
	"database/sql"
	"log"
	"os"
	"time"

	_ "github.com/lib/pq"
)

type ContactRepository interface {
	Save(*Contact) error
	FindByGroup(string) ([]Contact, error)
	FindByGroupAndId(string, int) (*Contact, error)
	FindByGroupAndLastName(string, string) ([]Contact, error)
	FindByGroupAndVersion(string, time.Time) ([]Contact, error)
	Update(*Contact) error
	DeleteByGroupAndId(string, int) error
}

func NewContactRepository() ContactRepository {
	repository := contactRepository{}
	if err := repository.connect(); err != nil {
		log.Fatal(err)
	}
	if err := repository.initialize(); err != nil {
		log.Fatal(err)
	}
	return &repository
}

type contactRepository struct {
	db *sql.DB
}

func (r *contactRepository) connect() (err error) {
	os.Getenv("")
	r.db, err = sql.Open("postgres", "postgresql://postgres@localhost:5432/golangrest?sslmode=disable")
	if err == nil {
		err = r.db.Ping()
	}
	return err
}
func (r *contactRepository) initialize() (err error) {
	var stmt *sql.Stmt
	for _, s := range schema {
		stmt, err = r.db.Prepare(s)
		if err == nil {
			_, err = stmt.Exec()
			if err != nil {
				return
			}
		}
	}
	return
}

func (r *contactRepository) Save(contact *Contact) error {
	const insert = `INSERT INTO contact(
						group_name,
						email_address,
						first_name,
						last_name,
						phone_number,
						personal_notes)
					VALUES ($1, $2, $3, $4, $5, $6)
					RETURNING id`
	return r.db.QueryRow(insert,
		contact.Group,
		contact.EmailAddress,
		contact.FirstName,
		contact.LastName,
		contact.PhoneNumber,
		contact.PersonalNotes,
	).Scan(&contact.Id)
}
func (r *contactRepository) FindByGroup(group string) ([]Contact, error) {
	rows, err := r.db.Query("SELECT * FROM contact WHERE group_name=$1", group)
	if err != nil {
		return nil, err
	}
	return r.scanRows(rows)
}
func (r *contactRepository) FindByGroupAndId(group string, id int) (*Contact, error) {
	row := r.db.QueryRow("SELECT * FROM contact WHERE group_name=$1 AND id=$2", group, id)
	return r.scanRow(row)
}
func (r *contactRepository) FindByGroupAndLastName(group, lastName string) ([]Contact, error) {
	rows, err := r.db.Query("SELECT * FROM contact WHERE group_name=$1 AND last_name=$2", group, lastName)
	if err != nil {
		return nil, err
	}
	return r.scanRows(rows)
}
func (r *contactRepository) FindByGroupAndVersion(group string, version time.Time) ([]Contact, error) {
	rows, err := r.db.Query("SELECT * FROM contact WHERE group_name=$1 AND last_modified>$2 ORDER BY last_modified", group, version)
	if err != nil {
		return nil, err
	}
	return r.scanRows(rows)
}
func (r *contactRepository) Update(contact *Contact) error {
	const update = `UPDATE contact SET
						group_name = $2,
						email_address = $3,
						first_name = $4,
						last_name = $5,
						phone_number = $6,
						personal_notes = $7
					WHERE id=$1`
	_, err := r.db.Exec(update,
		contact.Id,
		contact.Group,
		contact.EmailAddress,
		contact.FirstName,
		contact.LastName,
		contact.PhoneNumber,
		contact.PersonalNotes,
	)
	return err
}
func (r *contactRepository) DeleteByGroupAndId(group string, id int) error {
	_, err := r.db.Query("DELETE FROM contact WHERE group_name=$1 AND id=$2", group, id)
	return err
}

func (r *contactRepository) scanRow(row *sql.Row) (*Contact, error) {
	contact := new(Contact)
	err := row.Scan(
		&contact.Id,
		&contact.FirstName,
		&contact.LastName,
		&contact.EmailAddress,
		&contact.PhoneNumber,
		&contact.PersonalNotes,
		&contact.LastModified,
		&contact.Group,
	)
	if err != nil {
		return nil, err
	}
	return contact, nil
}
func (r *contactRepository) scanRows(rows *sql.Rows) ([]Contact, error) {
	contacts := make([]Contact, 0)
	for rows.Next() {
		contact := new(Contact)
		err := rows.Scan(
			&contact.Id,
			&contact.FirstName,
			&contact.LastName,
			&contact.EmailAddress,
			&contact.PhoneNumber,
			&contact.PersonalNotes,
			&contact.LastModified,
			&contact.Group,
		)
		if err != nil {
			return nil, err
		}
		contacts = append(contacts, *contact)
	}
	return contacts, nil
}

var schema = []string{
	`DROP TABLE IF EXISTS contact`,
	`DROP FUNCTION IF EXISTS trigger_set_timestamp`,
	`CREATE OR REPLACE FUNCTION trigger_set_timestamp()
		RETURNS TRIGGER LANGUAGE plpgsql AS
	$$BEGIN
		NEW.last_modified = NOW();
		RETURN NEW;
	END$$`,
	`CREATE TABLE IF NOT EXISTS contact
	(
		id             SERIAL PRIMARY KEY,
		first_name     VARCHAR(255) NOT NULL,
		last_name      VARCHAR(255) NOT NULL,
		email_address  VARCHAR(255) UNIQUE,
		phone_number   VARCHAR(255),
		personal_notes TEXT,
		last_modified  TIMESTAMP DEFAULT NOW(),
		group_name     VARCHAR(20) NOT NULL
	)`,
	`CREATE TRIGGER set_timestamp
		BEFORE UPDATE
		ON contact
		FOR EACH ROW
	EXECUTE FUNCTION trigger_set_timestamp()`,
}
