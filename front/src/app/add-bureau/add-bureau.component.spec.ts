import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddBureauComponent } from './add-bureau.component';

describe('AddBureauComponent', () => {
  let component: AddBureauComponent;
  let fixture: ComponentFixture<AddBureauComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddBureauComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddBureauComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
