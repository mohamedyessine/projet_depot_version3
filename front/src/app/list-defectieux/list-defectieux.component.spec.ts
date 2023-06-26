/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ListDefectieuxComponent } from './list-defectieux.component';

describe('ListDefectieuxComponent', () => {
  let component: ListDefectieuxComponent;
  let fixture: ComponentFixture<ListDefectieuxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListDefectieuxComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListDefectieuxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
